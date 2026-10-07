#define NOMINMAX
#include <windows.h>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <iostream>
#include <algorithm>
#include <cstdlib>
#include <cstring>
#include <random>
#include <string>
#include <vector>

#include "math_utils.h"
#include "shader.h"
#include "texture.h"
#include "camera.h"
#include "liminal.h"
#include "bunny.h"
#include "audio.h"
#include "stb_image.h"

static const int   kPaletteSteps = 12;
static const float kDitherStrength = 0.03f;
static float gFogDensity = 0.45f;
static const int   kRenderScale = 2;

static bool gHammerEdge = false;
static float gHammerAnim = 0.0f;

static void glfw_error_callback(int code, const char* desc) {
    std::cerr << "[GLFW] (" << code << ") " << desc << std::endl;
}

static void mouse_button_callback(GLFWwindow*, int button, int action, int) {
    if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
        gHammerEdge = true;
        gHammerAnim = 1.0f;
    }
}

static const char* kWindowTitles[] = {
    "System Idle Process", "Runtime Broker", "Windows Security", "dwm.exe",
    "NVIDIA Container", "SearchHost", "ShellExperienceHost", "ctfmon",
    "Untitled - Paint", "Not Responding", "C:\\Windows\\System32\\cmd.exe",
    "Task Manager", "Microsoft Edge", "explorer.exe", "NULL",
    "????", "meatboy", "dont look back", "HELP", "error 0x0000005"
};

static const char* kErrorBodies[] = {
    "EXCEPTION_ACCESS_VIOLATION reading location 0x00000000",
    "The instruction at 0x7FFE1234 referenced memory at 0xDEADBEEF",
    "KERNEL_SECURITY_CHECK_FAILURE",
    "IRQL_NOT_LESS_OR_EQUAL",
    "A critical process died",
    "MEMORY_MANAGEMENT",
    "SYSTEM_THREAD_EXCEPTION_NOT_HANDLED",
    "PAGE_FAULT_IN_NONPAGED_AREA",
    "its all your fault",
    "you should not have done that",
    "meatboy was here",
    "exposure limit exceeded",
    "Unexpected kernel mode trap",
    "DRIVER_IRQL_NOT_LESS_OR_EQUAL",
    "Status: 0xC0000005",
    "Fatal error in liminal runtime",
    "The application has failed to start correctly",
    "Unknown software exception 0x80000003",
};

static const char* kErrorCaptions[] = {
    "Windows", "ntdll.dll", "outofbound", "System Error",
    "Application Error", "Runtime Error", "STOP", "Fatal Exception",
    "csrss.exe", "winlogon.exe", "MEAT"
};

static std::string pick_random(const char* const* list, size_t count, std::mt19937& rng) {
    return list[std::uniform_int_distribution<size_t>(0, count - 1)(rng)];
}

struct LowResTarget {
    GLuint fbo = 0, color = 0, depth = 0;
    int w = 0, h = 0;
    void init(int fbw, int fbh, int scale) {
        int newW = std::max(160, fbw / std::max(1, scale));
        int newH = std::max(120, fbh / std::max(1, scale));
        if (newW == w && newH == h && color != 0) return;
        w = newW; h = newH;
        if (!fbo) glGenFramebuffers(1, &fbo);
        if (!color) glGenTextures(1, &color);
        if (!depth) glGenRenderbuffers(1, &depth);
        glBindTexture(GL_TEXTURE_2D, color);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindRenderbuffer(GL_RENDERBUFFER, depth);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, w, h);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, color, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depth);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    void destroy() {
        if (color) glDeleteTextures(1, &color);
        if (depth) glDeleteRenderbuffers(1, &depth);
        if (fbo) glDeleteFramebuffers(1, &fbo);
        color = depth = fbo = 0;
    }
};

static const char* HUD_VERT = R"(
#version 330 core
layout(location=0) in vec2 aPos;
layout(location=1) in vec2 aUV;
out vec2 vUV;
uniform mat4 uMVP;
void main(){ vUV=aUV; gl_Position=uMVP*vec4(aPos,0.0,1.0); }
)";
static const char* HUD_FRAG = R"(
#version 330 core
in vec2 vUV; out vec4 FragColor;
uniform sampler2D uTex;
void main(){
  vec4 t=texture(uTex,vUV);
  if(t.a<0.15) discard;
  FragColor=t;
}
)";

struct HammerHud {
    GLuint tex = 0;
    GLuint vao = 0, vbo = 0;
    Shader shader;
    GLint loc_mvp = -1, loc_tex = -1;
    bool ok = false;

    bool load() {
        stbi_set_flip_vertically_on_load(0);
        int w,h,c;
        std::string path = resolve_asset_path("hammer.png");
        unsigned char* data = stbi_load(path.c_str(), &w, &h, &c, 4);
        if (!data) {
            std::cerr << "[Hammer] missing " << path << std::endl;
            return false;
        }
        glGenTextures(1, &tex);
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        stbi_image_free(data);
        if (!shader.load(HUD_VERT, HUD_FRAG)) return false;
        loc_mvp = shader.get_uniform("uMVP");
        loc_tex = shader.get_uniform("uTex");
        glGenVertexArrays(1, &vao);
        glGenBuffers(1, &vbo);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 6 * 4 * sizeof(float), nullptr, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)(2 * sizeof(float)));
        glEnableVertexAttribArray(1);
        glBindVertexArray(0);
        ok = true;
        return true;
    }

    void draw(int fbw, int fbh, float swing) {
        if (!ok) return;
        float aspect = fbw / static_cast<float>(fbh > 0 ? fbh : 1);

        float size = 0.55f;
        float cx = 0.62f + swing * 0.08f;
        float cy = -0.55f + swing * 0.25f;
        float rot = -0.35f - swing * 1.1f;
        float c = std::cos(rot), s = std::sin(rot);
        float hx = size * 0.55f;
        float hy = size;
        auto xf = [&](float x, float y, float& ox, float& oy) {
            float rx = x * c - y * s;
            float ry = x * s + y * c;
            ox = cx + rx;
            oy = cy + ry / aspect;
        };
        float x0,y0,x1,y1,x2,y2,x3,y3;
        xf(-hx,  hy, x0,y0);
        xf(-hx, -hy, x1,y1);
        xf( hx, -hy, x2,y2);
        xf( hx,  hy, x3,y3);
        float verts[6*4] = {
            x0,y0, 0,0,  x1,y1, 0,1,  x2,y2, 1,1,
            x0,y0, 0,0,  x2,y2, 1,1,  x3,y3, 1,0
        };
        Mat4 id{};
        for (int i = 0; i < 16; ++i) id.m[i] = 0.0f;
        id.m[0] = id.m[5] = id.m[10] = id.m[15] = 1.0f;

        glDisable(GL_DEPTH_TEST);
        shader.use();
        shader.set_mat4(loc_mvp, id.m);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex);
        shader.set_int(loc_tex, 0);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(verts), verts);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
        glEnable(GL_DEPTH_TEST);
    }

    void destroy() {
        if (tex) glDeleteTextures(1, &tex);
        if (vbo) glDeleteBuffers(1, &vbo);
        if (vao) glDeleteVertexArrays(1, &vao);
        tex = vao = vbo = 0;
    }
};

int main() {
    std::mt19937 rng{std::random_device{}()};

    glfwSetErrorCallback(glfw_error_callback);
    if (!glfwInit()) return -1;

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_SAMPLES, 0);

    std::string winTitle = pick_random(kWindowTitles, sizeof(kWindowTitles)/sizeof(kWindowTitles[0]), rng);
    GLFWwindow* window = glfwCreateWindow(1280, 720, winTitle.c_str(), nullptr, nullptr);
    if (!window) { glfwTerminate(); return -1; }
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwSetMouseButtonCallback(window, mouse_button_callback);

    glewExperimental = GL_TRUE;
    if (glewInit() != GLEW_OK) return -1;
    glGetError();

    AudioSystem audio;
    bool hasAudio = audio.init();
    if (hasAudio) {
        audio.play_music("noise.mp3", 0.5f);
        audio.load_sound("hammer_swing", "hammer.ogg", false);
        audio.load_sound("hammer_hit", "hammer_hit.ogg", false);
    }

    Shader shader;
    if (!shader.load(TUNNEL_VERT_SRC, TUNNEL_FRAG_SRC)) return -1;
    GLint locMvp = shader.get_uniform("uMVP");
    GLint locFogColor = shader.get_uniform("uFogColor");
    GLint locFogDensity = shader.get_uniform("uFogDensity");
    GLint locPalette = shader.get_uniform("uPaletteSteps");
    GLint locDither = shader.get_uniform("uDitherStrength");
    GLint locTex = shader.get_uniform("uTex");

    LiminalSpace space;
    space.config.texture_name = "meat.png";
    space.generate();

    Texture wall_texture;
    if (!wall_texture.load("meat.png")) wall_texture.load_fallback();

    HammerHud hammer;
    hammer.load();

    glEnable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    int fbw, fbh;
    glfwGetFramebufferSize(window, &fbw, &fbh);
    LowResTarget lowRes;
    bool useLowRes = true;
    lowRes.init(fbw, fbh, kRenderScale);

    Camera camera;
    camera.x = space.spawn_x;
    camera.z = space.spawn_z;
    camera.set_floor(-space.config.half_height);
    camera.y = -space.config.half_height + camera.player_height;
    camera.walking_enabled = true;
    const float kPlayerRadius = 30.0f;

    BunnyHunt hunt;
    float exposure = 0.0f;
    if (hunt.load("bunny.png")) {
        hunt.spawn(space, camera.x, camera.z, -space.config.half_height, 8);
    }

    double lastTime = glfwGetTime();
    double nextTitleSwap = lastTime + 8.0;
    bool won = false;

    while (!glfwWindowShouldClose(window)) {
        double now = glfwGetTime();
        float dt = static_cast<float>(now - lastTime);
        lastTime = now;
        if (dt > 0.05f) dt = 0.05f;

        if (now >= nextTitleSwap) {
            winTitle = pick_random(kWindowTitles, sizeof(kWindowTitles)/sizeof(kWindowTitles[0]), rng);
            glfwSetWindowTitle(window, winTitle.c_str());
            nextTitleSwap = now + std::uniform_real_distribution<float>(4.0f, 14.0f)(rng);
        }

        glfwPollEvents();
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, 1);
        }

        if (gHammerAnim > 0.0f) {
            gHammerAnim = std::max(0.0f, gHammerAnim - dt * 3.5f);
        }

        camera.process_input(window, dt);
        space.resolve_collision(camera.x, camera.z, kPlayerRadius);
        hunt.update(dt, camera.x, camera.z, space);

        if (gHammerEdge) {
            gHammerEdge = false;
            if (hasAudio) audio.play_sound("hammer_swing");
            if (hunt.try_hammer(camera.x, camera.y, camera.z, camera.yaw)) {
                if (hasAudio) audio.play_sound("hammer_hit");
                exposure = std::min(1.0f, exposure + 0.22f);
                gFogDensity = 0.45f + exposure * 0.7f;
                if (hunt.kills >= BunnyHunt::kWinKills) {
                    won = true;
                    glfwSetWindowShouldClose(window, 1);
                }
            }
        }

        glfwGetFramebufferSize(window, &fbw, &fbh);
        lowRes.init(fbw, fbh, kRenderScale);
        glBindFramebuffer(GL_FRAMEBUFFER, useLowRes ? lowRes.fbo : 0);
        glViewport(0, 0, useLowRes ? lowRes.w : fbw, useLowRes ? lowRes.h : fbh);

        float cr = 0.04f + exposure * 0.35f;
        float cg = 0.01f + exposure * 0.02f;
        float cb = 0.01f;
        glClearColor(cr, cg, cb, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float aspect = fbw / static_cast<float>(fbh > 0 ? fbh : 1);
        Mat4 proj = mat4_perspective(60.0f * DEG_TO_RAD, aspect, 0.1f, 40000.0f);
        Mat4 view = camera.get_view_matrix();
        Mat4 mvp = mat4_mul(proj, view);

        shader.use();
        wall_texture.bind(0);
        shader.set_int(locTex, 0);
        shader.set_mat4(locMvp, mvp.m);
        shader.set_vec3(locFogColor, cr, cg, cb);
        shader.set_float(locFogDensity, gFogDensity);
        shader.set_float(locPalette, static_cast<float>(kPaletteSteps));
        shader.set_float(locDither, kDitherStrength + exposure * 0.04f);
        space.draw();
        hunt.render(mvp, camera.x, camera.y, camera.z, exposure);

        if (useLowRes) {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, lowRes.fbo);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
            glBlitFramebuffer(0, 0, lowRes.w, lowRes.h, 0, 0, fbw, fbh, GL_COLOR_BUFFER_BIT, GL_NEAREST);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0, 0, fbw, fbh);
        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        hammer.draw(fbw, fbh, gHammerAnim);

        glfwSwapBuffers(window);
    }

    audio.shutdown();
    hammer.destroy();
    lowRes.destroy();
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    if (won) {
        std::string body = pick_random(kErrorBodies, sizeof(kErrorBodies)/sizeof(kErrorBodies[0]), rng);
        std::string cap = pick_random(kErrorCaptions, sizeof(kErrorCaptions)/sizeof(kErrorCaptions[0]), rng);
        MessageBoxA(nullptr, body.c_str(), cap.c_str(),
                    MB_OK | MB_ICONERROR | MB_TOPMOST | MB_SETFOREGROUND);
    }
    glfwTerminate();
    return 0;
}
