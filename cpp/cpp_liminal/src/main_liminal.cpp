#define NOMINMAX
#include <windows.h>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <iostream>
#include <algorithm>
#include <cstdlib>

#include "math_utils.h"
#include "shader.h"
#include "texture.h"
#include "camera.h"
#include "liminal.h"
#include "eye.h"
#include "audio.h"

static const int   kPaletteSteps = 12;
static const float kDitherStrength = 0.03f;
static const float kFogDensity = 0.5f;
static const int   kRenderScale = 2;

static void glfw_error_callback(int code, const char* desc) {
    std::cerr << "[GLFW] (" << code << ") " << desc << std::endl;
}

static void APIENTRY gl_debug_callback(GLenum source, GLenum type, GLuint id, GLenum severity,
                                       GLsizei, const GLchar* message, const void*) {
    (void)source;
    (void)type;
    if (severity == GL_DEBUG_SEVERITY_NOTIFICATION) return;
    std::cerr << "[GL] (" << id << ") " << message << std::endl;
}

struct LowResTarget {
    GLuint fbo = 0;
    GLuint color = 0;
    GLuint depth = 0;
    int w = 0;
    int h = 0;

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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

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

int main() {
    glfwSetErrorCallback(glfw_error_callback);
    if (!glfwInit()) {
        std::cerr << "Failed to init GLFW" << std::endl;
        return -1;
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_SAMPLES, 0);
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

    GLFWwindow* window = glfwCreateWindow(1280, 720, "HELP ME PLEASE", nullptr, nullptr);
    if (!window) {
        std::cerr << "Failed to create window" << std::endl;
        glfwTerminate();
        return -1;
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    std::cerr << "[Init] Window/context created" << std::endl;

    glewExperimental = GL_TRUE;
    if (glewInit() != GLEW_OK) {
        std::cerr << "Failed to init GLEW" << std::endl;
        return -1;
    }
    glGetError();

    if (GLEW_KHR_debug || GLEW_ARB_debug_output) {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(gl_debug_callback, nullptr);
    }

    AudioSystem audio;
    if (audio.init()) {
        audio.play_music("noise.mp3", 0.6f);
    }

    Shader shader;
    if (!shader.load(TUNNEL_VERT_SRC, TUNNEL_FRAG_SRC)) {
        std::cerr << "Shader failed to load/link. Exiting." << std::endl;
        return -1;
    }

    GLint locMvp = shader.get_uniform("uMVP");
    GLint locFogColor = shader.get_uniform("uFogColor");
    GLint locFogDensity = shader.get_uniform("uFogDensity");
    GLint locPalette = shader.get_uniform("uPaletteSteps");
    GLint locDither = shader.get_uniform("uDitherStrength");
    GLint locTex = shader.get_uniform("uTex");

    LiminalSpace space;
    space.generate();

    Texture wall_texture;
    wall_texture.load_fallback();

    glEnable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);

    int fbw, fbh;
    glfwGetFramebufferSize(window, &fbw, &fbh);
    LowResTarget lowRes;
    bool useLowRes = true;
    lowRes.init(fbw, fbh, kRenderScale);
    glBindFramebuffer(GL_FRAMEBUFFER, lowRes.fbo);
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        useLowRes = false;
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    Camera camera;
    camera.x = space.spawn_x;
    camera.z = space.spawn_z;
    camera.set_floor(-space.config.half_height);
    camera.y = -space.config.half_height + camera.player_height;
    camera.walking_enabled = true;
    const float kPlayerRadius = 30.0f;

    Eye eye;
    if (eye.load("eye.gif")) {
        eye.reset(space, camera.x, camera.z, -space.config.half_height);
    }

    double lastTime = glfwGetTime();
    double startTime = glfwGetTime();
    const double kMaxDuration = 300.0;

    while (!glfwWindowShouldClose(window)) {
        double now = glfwGetTime();
        float dt = static_cast<float>(now - lastTime);
        lastTime = now;

        glfwPollEvents();

        if (now - startTime >= kMaxDuration) {
            glfwSetWindowShouldClose(window, 1);
        }

        camera.process_input(window, dt);
        space.resolve_collision(camera.x, camera.z, kPlayerRadius);

        eye.update(dt, camera.x, camera.z, space);
        if (eye.caught) {
            audio.shutdown();
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            MessageBoxA(nullptr, "its all your fault", "outofbound",
                        MB_OK | MB_ICONERROR | MB_TOPMOST | MB_SETFOREGROUND);
            std::exit(1);
        }

        glfwGetFramebufferSize(window, &fbw, &fbh);
        lowRes.init(fbw, fbh, kRenderScale);
        glBindFramebuffer(GL_FRAMEBUFFER, useLowRes ? lowRes.fbo : 0);
        glViewport(0, 0, useLowRes ? lowRes.w : fbw, useLowRes ? lowRes.h : fbh);
        glClearColor(0.02f, 0.015f, 0.025f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float aspect = fbw / static_cast<float>(fbh > 0 ? fbh : 1);
        Mat4 proj = mat4_perspective(60.0f * DEG_TO_RAD, aspect, 0.1f, 40000.0f);
        Mat4 view = camera.get_view_matrix();
        Mat4 mvp = mat4_mul(proj, view);

        shader.use();
        wall_texture.bind(0);
        shader.set_int(locTex, 0);
        shader.set_mat4(locMvp, mvp.m);
        shader.set_vec3(locFogColor, 0.02f, 0.015f, 0.025f);
        shader.set_float(locFogDensity, kFogDensity);
        shader.set_float(locPalette, static_cast<float>(kPaletteSteps));
        shader.set_float(locDither, kDitherStrength);

        space.draw();
        eye.render(mvp, camera.x, camera.y, camera.z);

        if (useLowRes) {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, lowRes.fbo);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
            glBlitFramebuffer(0, 0, lowRes.w, lowRes.h, 0, 0, fbw, fbh, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        }

        glfwSwapBuffers(window);
    }

    audio.shutdown();
    lowRes.destroy();
    glfwTerminate();
    return 0;
}
