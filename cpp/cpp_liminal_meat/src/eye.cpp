#include "eye.h"

#include "liminal.h"
#include "texture.h"
#include "stb_image.h"

#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <random>

namespace {

const float kFogColor[3] = { 0.02f, 0.015f, 0.025f };
const float kFogDensity = 0.3f;

const char* EYE_VERT_SRC = R"(
#version 330 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aUV;

uniform mat4 uMVP;
out vec2 vUV;
out float vDepth;

void main() {
    vec4 clip = uMVP * vec4(aPos, 1.0);
    gl_Position = clip;
    vUV = aUV;
    vDepth = clip.z / clip.w;
}
)";

const char* EYE_FRAG_SRC = R"(
#version 330 core
in vec2 vUV;
in float vDepth;
out vec4 FragColor;

uniform sampler2D uTex;
uniform vec3 uFogColor;
uniform float uFogDensity;

void main() {
    vec4 texel = texture(uTex, vUV);
    if (texel.a < 0.5) discard;

    vec3 color = texel.rgb;
    float fogFactor = clamp(vDepth * uFogDensity, 0.0, 0.85);
    color = mix(color, uFogColor, fogFactor);
    FragColor = vec4(color, 1.0);
}
)";
}

Eye::~Eye() {
    destroy();
}

void Eye::destroy() {
    if (!frames.empty()) {
        glDeleteTextures(static_cast<GLsizei>(frames.size()), frames.data());
        frames.clear();
    }
    frame_delays.clear();
    if (vbo) { glDeleteBuffers(1, &vbo); vbo = 0; }
    if (vao) { glDeleteVertexArrays(1, &vao); vao = 0; }
}

bool Eye::load(const std::string& gif_filename) {
    std::string path = resolve_asset_path(gif_filename);

    FILE* fp = std::fopen(path.c_str(), "rb");
    if (!fp) {
        std::cerr << "[Eye] Failed to open " << path << std::endl;
        return false;
    }
    std::fseek(fp, 0, SEEK_END);
    long file_size = std::ftell(fp);
    std::fseek(fp, 0, SEEK_SET);
    if (file_size <= 0) {
        std::fclose(fp);
        return false;
    }
    std::vector<unsigned char> bytes(static_cast<size_t>(file_size));
    size_t read = std::fread(bytes.data(), 1, bytes.size(), fp);
    std::fclose(fp);
    if (read != bytes.size()) {
        return false;
    }

    int* delays = nullptr;
    int w = 0, h = 0, frame_count = 0, comp = 0;
    unsigned char* data = stbi_load_gif_from_memory(bytes.data(), static_cast<int>(bytes.size()),
                                                     &delays, &w, &h, &frame_count, &comp, 4);
    if (!data || frame_count <= 0 || w <= 0 || h <= 0) {
        std::cerr << "[Eye] Failed to decode gif " << path << std::endl;
        if (data) stbi_image_free(data);
        if (delays) std::free(delays);
        return false;
    }

    frames.resize(static_cast<size_t>(frame_count), 0);
    frame_delays.resize(static_cast<size_t>(frame_count), 0.08f);
    total_duration = 0.0f;

    const int frame_bytes = w * h * 4;
    glGenTextures(frame_count, frames.data());
    for (int i = 0; i < frame_count; ++i) {
        float delay = (delays && delays[i] > 0) ? (delays[i] / 1000.0f) : 0.08f;
        frame_delays[static_cast<size_t>(i)] = delay;
        total_duration += delay;

        glBindTexture(GL_TEXTURE_2D, frames[static_cast<size_t>(i)]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                     data + static_cast<size_t>(i) * frame_bytes);
    }

    stbi_image_free(data);
    if (delays) std::free(delays);
    if (total_duration <= 0.001f) total_duration = frame_count * 0.08f;

    if (!shader.load(EYE_VERT_SRC, EYE_FRAG_SRC)) {
        std::cerr << "[Eye] Billboard shader failed to link" << std::endl;
        return false;
    }
    loc_mvp = shader.get_uniform("uMVP");
    loc_tex = shader.get_uniform("uTex");
    loc_fog_color = shader.get_uniform("uFogColor");
    loc_fog_density = shader.get_uniform("uFogDensity");

    glGenVertexArrays(1, &vao);
    glGenBuffers(1, &vbo);
    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, 6 * 5 * sizeof(float), nullptr, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    glEnableVertexAttribArray(1);
    glBindVertexArray(0);

    std::cerr << "[Eye] Loaded " << frame_count << " frames (" << w << "x" << h << ") from " << path << std::endl;
    return true;
}

void Eye::reset(const LiminalSpace& space, float from_x, float from_z, float floor_y) {
    caught = false;
    anim_time = 0.0f;
    y = floor_y + size;

    float half_w = space.world_width * 0.5f;
    std::mt19937 rng(std::random_device{}());
    std::uniform_real_distribution<float> rx(-half_w + radius, half_w - radius);
    std::uniform_real_distribution<float> rz(radius, space.world_length - radius);

    float min_dist = space.world_width * 0.12f;
    float max_dist = space.world_width * 0.22f;
    float min_dist2 = min_dist * min_dist;
    float max_dist2 = max_dist * max_dist;

    float best_x = from_x;
    float best_z = from_z;
    float best_dist = -1.0f;
    bool found = false;
    for (int attempt = 0; attempt < 512; ++attempt) {
        float cx = rx(rng);
        float cz = rz(rng);
        if (space.is_wall_at(cx, cz)) continue;
        float dx = cx - from_x;
        float dz = cz - from_z;
        float dist = dx * dx + dz * dz;
        if (dist >= min_dist2 && dist <= max_dist2) {
            x = cx;
            z = cz;
            found = true;
            break;
        }

        if (dist > best_dist) {
            best_dist = dist;
            best_x = cx;
            best_z = cz;
        }
    }

    if (!found) {
        x = best_x;
        z = best_z;
    }
}

void Eye::update(float dt, float player_x, float player_z, const LiminalSpace& space) {
    anim_time += dt;

    float dx = player_x - x;
    float dz = player_z - z;
    float dist = std::sqrt(dx * dx + dz * dz);
    if (dist > 1e-3f) {
        x += (dx / dist) * speed * dt;
        z += (dz / dist) * speed * dt;
    }
    space.resolve_collision(x, z, radius);

    if (dist <= catch_distance) {
        caught = true;
    }
}

GLuint Eye::current_frame_tex() const {
    if (frames.empty()) return 0;
    float t = std::fmod(anim_time, total_duration);
    if (t < 0.0f) t += total_duration;
    float acc = 0.0f;
    for (size_t i = 0; i < frames.size(); ++i) {
        acc += frame_delays[i];
        if (t <= acc) return frames[i];
    }
    return frames.back();
}

void Eye::render(const Mat4& view_proj, float cam_x, float cam_y, float cam_z) {
    if (frames.empty()) return;

    float fx = cam_x - x;
    float fz = cam_z - z;
    float flen = std::sqrt(fx * fx + fz * fz);
    if (flen < 1e-4f) flen = 1.0f;
    fx /= flen;
    fz /= flen;
    float rightx = fz;
    float rightz = -fx;

    (void)cam_y;

    float rx = rightx * size;
    float rz = rightz * size;
    float uy = size;

    float cx = x, cy = y, cz = z;

    float verts[6 * 5] = {
        cx - rx, cy + uy, cz - rz, 0.0f, 0.0f,
        cx - rx, cy - uy, cz - rz, 0.0f, 1.0f,
        cx + rx, cy - uy, cz + rz, 1.0f, 1.0f,

        cx - rx, cy + uy, cz - rz, 0.0f, 0.0f,
        cx + rx, cy - uy, cz + rz, 1.0f, 1.0f,
        cx + rx, cy + uy, cz + rz, 1.0f, 0.0f,
    };

    shader.use();
    shader.set_mat4(loc_mvp, view_proj.m);
    shader.set_vec3(loc_fog_color, kFogColor[0], kFogColor[1], kFogColor[2]);
    shader.set_float(loc_fog_density, kFogDensity);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, current_frame_tex());
    shader.set_int(loc_tex, 0);

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(verts), verts);
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glBindVertexArray(0);
}
