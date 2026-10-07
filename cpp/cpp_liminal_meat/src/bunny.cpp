#include "bunny.h"

#include "liminal.h"
#include "texture.h"
#include "stb_image.h"

#include <algorithm>
#include <cmath>
#include <iostream>

namespace {
const char* BUNNY_VERT = R"(
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

const char* BUNNY_FRAG = R"(
#version 330 core
in vec2 vUV;
in float vDepth;
out vec4 FragColor;
uniform sampler2D uTex;
uniform vec3 uFogColor;
uniform float uFogDensity;
uniform float uExposure;
void main() {
    vec4 texel = texture(uTex, vUV);
    if (texel.a < 0.4) discard;
    vec3 color = texel.rgb * (1.0 + uExposure * 1.8);
    float fogFactor = clamp(vDepth * uFogDensity, 0.0, 0.9);
    color = mix(color, uFogColor, fogFactor);
    FragColor = vec4(color, 1.0);
}
)";
}

BunnyHunt::~BunnyHunt() { destroy(); }

void BunnyHunt::destroy() {
    if (bunny_tex) { glDeleteTextures(1, &bunny_tex); bunny_tex = 0; }
    if (!particle_tex.empty()) {
        glDeleteTextures(static_cast<GLsizei>(particle_tex.size()), particle_tex.data());
        particle_tex.clear();
    }
    if (vbo) { glDeleteBuffers(1, &vbo); vbo = 0; }
    if (vao) { glDeleteVertexArrays(1, &vao); vao = 0; }
    bunnies.clear();
    gibs.clear();
}

bool BunnyHunt::load(const std::string& bunny_png) {
    stbi_set_flip_vertically_on_load(0);
    int w = 0, h = 0, ch = 0;
    std::string path = resolve_asset_path(bunny_png);
    unsigned char* data = stbi_load(path.c_str(), &w, &h, &ch, 4);
    if (!data) {
        std::cerr << "[Bunny] Failed to load " << path << std::endl;
        return false;
    }
    glGenTextures(1, &bunny_tex);
    glBindTexture(GL_TEXTURE_2D, bunny_tex);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    stbi_image_free(data);

    for (int i = 0; i < 8; ++i) {
        std::string pp = resolve_asset_path("particles/p" + std::to_string(i) + ".png");
        int pw, ph, pc;
        unsigned char* pdata = stbi_load(pp.c_str(), &pw, &ph, &pc, 4);
        if (!pdata) continue;
        GLuint tex = 0;
        glGenTextures(1, &tex);
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, pw, ph, 0, GL_RGBA, GL_UNSIGNED_BYTE, pdata);
        stbi_image_free(pdata);
        particle_tex.push_back(tex);
    }
    if (particle_tex.empty()) {
        particle_tex.push_back(bunny_tex);
    }

    if (!shader.load(BUNNY_VERT, BUNNY_FRAG)) {
        std::cerr << "[Bunny] shader failed" << std::endl;
        return false;
    }
    loc_mvp = shader.get_uniform("uMVP");
    loc_tex = shader.get_uniform("uTex");
    loc_fog_color = shader.get_uniform("uFogColor");
    loc_fog_density = shader.get_uniform("uFogDensity");
    loc_exposure = shader.get_uniform("uExposure");

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

    std::cerr << "[Bunny] loaded " << w << "x" << h << " + " << particle_tex.size() << " particles" << std::endl;
    return true;
}

void BunnyHunt::spawn(const LiminalSpace& space, float from_x, float from_z, float floor_y_, int count) {
    floor_y = floor_y_;
    bunnies.clear();
    kills = 0;
    float half_w = space.world_width * 0.5f;
    std::uniform_real_distribution<float> rx(-half_w + radius, half_w - radius);
    std::uniform_real_distribution<float> rz(radius, space.world_length - radius);

    for (int n = 0; n < count; ++n) {
        Bunny b;
        b.y = floor_y + size;
        b.alive = true;
        bool placed = false;
        for (int a = 0; a < 256; ++a) {
            float cx = rx(rng);
            float cz = rz(rng);
            if (space.is_wall_at(cx, cz)) continue;
            float dx = cx - from_x;
            float dz = cz - from_z;
            if (dx * dx + dz * dz < 200.0f * 200.0f) continue;
            b.x = cx;
            b.z = cz;
            placed = true;
            break;
        }
        if (!placed) {
            b.x = from_x + 400.0f * (n + 1);
            b.z = from_z + 300.0f;
        }
        float ang = std::uniform_real_distribution<float>(0.0f, 6.28318f)(rng);
        b.vx = std::cos(ang) * speed;
        b.vz = std::sin(ang) * speed;
        b.wander_timer = std::uniform_real_distribution<float>(0.4f, 1.6f)(rng);
        bunnies.push_back(b);
    }
}

int BunnyHunt::alive_count() const {
    int n = 0;
    for (const auto& b : bunnies) if (b.alive) ++n;
    return n;
}

void BunnyHunt::update(float dt, float player_x, float player_z, const LiminalSpace& space) {
    for (auto& b : bunnies) {
        if (!b.alive) continue;
        b.wander_timer -= dt;
        if (b.wander_timer <= 0.0f) {

            float dx = player_x - b.x;
            float dz = player_z - b.z;
            float dist = std::sqrt(dx * dx + dz * dz);
            float ang = std::atan2(dz, dx);
            if (dist < 350.0f) {

                ang += (rng() & 1) ? 1.8f : -1.8f;
            } else {
                ang += std::uniform_real_distribution<float>(-0.8f, 0.8f)(rng);
            }
            float spd = speed * std::uniform_real_distribution<float>(0.85f, 1.25f)(rng);
            b.vx = std::cos(ang) * spd;
            b.vz = std::sin(ang) * spd;
            b.wander_timer = std::uniform_real_distribution<float>(0.35f, 1.2f)(rng);
        }
        b.x += b.vx * dt;
        b.z += b.vz * dt;
        space.resolve_collision(b.x, b.z, radius);
    }

    for (auto& g : gibs) {
        g.life -= dt;
        g.x += g.vx * dt;
        g.y += g.vy * dt;
        g.z += g.vz * dt;
        g.vy -= 400.0f * dt;
        if (g.y < floor_y + 10.0f) {
            g.y = floor_y + 10.0f;
            g.vy *= -0.3f;
        }
    }
    gibs.erase(std::remove_if(gibs.begin(), gibs.end(),
                              [](const Gib& g) { return g.life <= 0.0f; }),
               gibs.end());
}

void BunnyHunt::spawn_gibs(float x, float y, float z) {
    int n = particle_tex.empty() ? 0 : static_cast<int>(particle_tex.size());
    if (n <= 0) return;
    for (int i = 0; i < 14; ++i) {
        Gib g;
        g.x = x; g.y = y; g.z = z;
        g.vx = std::uniform_real_distribution<float>(-220.0f, 220.0f)(rng);
        g.vy = std::uniform_real_distribution<float>(80.0f, 320.0f)(rng);
        g.vz = std::uniform_real_distribution<float>(-220.0f, 220.0f)(rng);
        g.life = std::uniform_real_distribution<float>(0.7f, 1.6f)(rng);
        g.tex_index = i % n;
        gibs.push_back(g);
    }
}

bool BunnyHunt::try_hammer(float player_x, float player_y, float player_z, float yaw) {
    (void)player_y;
    float fx = std::sin(yaw);
    float fz = std::cos(yaw);
    float best_dist = melee_range;
    int best = -1;
    for (int i = 0; i < static_cast<int>(bunnies.size()); ++i) {
        auto& b = bunnies[static_cast<size_t>(i)];
        if (!b.alive) continue;
        float dx = b.x - player_x;
        float dz = b.z - player_z;
        float dist = std::sqrt(dx * dx + dz * dz);
        if (dist > melee_range) continue;
        float nd = dist > 1e-3f ? (dx * fx + dz * fz) / dist : 1.0f;
        if (nd < 0.35f) continue;
        if (dist < best_dist) {
            best_dist = dist;
            best = i;
        }
    }
    if (best < 0) return false;
    auto& b = bunnies[static_cast<size_t>(best)];
    b.alive = false;
    spawn_gibs(b.x, b.y, b.z);
    ++kills;
    return true;
}

void BunnyHunt::draw_billboard(GLuint tex, float x, float y, float z, float half,
                               const Mat4& view_proj, float cam_x, float cam_z,
                               float exposure, float fog) {
    float fx = cam_x - x;
    float fz = cam_z - z;
    float flen = std::sqrt(fx * fx + fz * fz);
    if (flen < 1e-4f) flen = 1.0f;
    fx /= flen; fz /= flen;
    float rightx = fz, rightz = -fx;
    float rx = rightx * half, rz = rightz * half, uy = half;

    float verts[6 * 5] = {
        x - rx, y + uy, z - rz, 0.0f, 0.0f,
        x - rx, y - uy, z - rz, 0.0f, 1.0f,
        x + rx, y - uy, z + rz, 1.0f, 1.0f,
        x - rx, y + uy, z - rz, 0.0f, 0.0f,
        x + rx, y - uy, z + rz, 1.0f, 1.0f,
        x + rx, y + uy, z + rz, 1.0f, 0.0f,
    };

    shader.use();
    shader.set_mat4(loc_mvp, view_proj.m);
    float fog_r = 0.12f + exposure * 0.25f;
    float fog_g = 0.02f;
    float fog_b = 0.02f;
    shader.set_vec3(loc_fog_color, fog_r, fog_g, fog_b);
    shader.set_float(loc_fog_density, fog);
    shader.set_float(loc_exposure, exposure);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex);
    shader.set_int(loc_tex, 0);

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(verts), verts);
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glBindVertexArray(0);
}

void BunnyHunt::render(const Mat4& view_proj, float cam_x, float cam_y, float cam_z, float exposure) {
    (void)cam_y;
    float fog = 0.35f + exposure * 0.55f;
    for (const auto& b : bunnies) {
        if (!b.alive || !bunny_tex) continue;
        draw_billboard(bunny_tex, b.x, b.y, b.z, size, view_proj, cam_x, cam_z, exposure, fog);
    }
    for (const auto& g : gibs) {
        if (g.tex_index < 0 || g.tex_index >= static_cast<int>(particle_tex.size())) continue;
        draw_billboard(particle_tex[static_cast<size_t>(g.tex_index)],
                       g.x, g.y, g.z, 28.0f, view_proj, cam_x, cam_z, exposure, fog * 0.7f);
    }
}
