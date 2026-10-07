#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <iostream>
#include <algorithm>

#include "math_utils.h"
#include "shader.h"
#include "texture.h"
#include "camera.h"
#include "tunnel.h"
#include "audio.h"

static const int   kPaletteSteps = 12;
static const float kDitherStrength = 0.03f;
static const float kFogDensity = 0.5f;
static const int   kRenderScale = 2;

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

    if (!glfwInit()) {
        std::cerr << "Failed to init GLFW" << std::endl;
        return -1;
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_SAMPLES, 0);

    GLFWwindow* window = glfwCreateWindow(1280, 720, "Why did you leave me here?", nullptr, nullptr);
    if (!window) {
        std::cerr << "Failed to create window" << std::endl;
        glfwTerminate();
        return -1;
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    if (glewInit() != GLEW_OK) {
        std::cerr << "Failed to init GLEW" << std::endl;
        return -1;
    }

    AudioSystem audio;
    if (audio.init()) {

        audio.play_music("slowed_soundtrack.wav", 0.4f);

        audio.load_sound("breathing", "breathing.wav", true);
        audio.set_volume("breathing", 0.6f);
        audio.play_sound("breathing");
    }

    Shader shader;
    shader.load(TUNNEL_VERT_SRC, TUNNEL_FRAG_SRC);

    GLint locMvp = shader.get_uniform("uMVP");
    GLint locFogColor = shader.get_uniform("uFogColor");
    GLint locFogDensity = shader.get_uniform("uFogDensity");
    GLint locPalette = shader.get_uniform("uPaletteSteps");
    GLint locDither = shader.get_uniform("uDitherStrength");
    GLint locTex = shader.get_uniform("uTex");

    Texture wall_texture;
    wall_texture.load("texture.jpg");

    Tunnel tunnel;
    tunnel.generate();

    glEnable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);

    int fbw, fbh;
    glfwGetFramebufferSize(window, &fbw, &fbh);
    LowResTarget lowRes;
    lowRes.init(fbw, fbh, kRenderScale);

    Camera camera;
    camera.z = 100.0f;
    camera.set_floor(-tunnel.config.half_size);
    camera.y = -tunnel.config.half_size + camera.player_height;
    camera.walking_enabled = true;

    double lastTime = glfwGetTime();
    double startTime = glfwGetTime();
    const double kMaxDuration = 240.0;

    while (!glfwWindowShouldClose(window)) {
        double now = glfwGetTime();
        float dt = static_cast<float>(now - lastTime);
        lastTime = now;

        glfwPollEvents();

        if (now - startTime >= kMaxDuration) {
            glfwSetWindowShouldClose(window, 1);
        }

        camera.process_input(window, dt);

        float limit = tunnel.config.half_size - 20.0f;
        camera.x = std::clamp(camera.x, -limit, limit);
        camera.z = std::clamp(camera.z, 10.0f, tunnel.config.length - 50.0f);

        glfwGetFramebufferSize(window, &fbw, &fbh);
        lowRes.init(fbw, fbh, kRenderScale);

        glBindFramebuffer(GL_FRAMEBUFFER, lowRes.fbo);
        glViewport(0, 0, lowRes.w, lowRes.h);
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

        tunnel.draw();

        glBindFramebuffer(GL_READ_FRAMEBUFFER, lowRes.fbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, lowRes.w, lowRes.h, 0, 0, fbw, fbh, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        glfwSwapBuffers(window);
    }

    audio.shutdown();
    lowRes.destroy();
    glfwTerminate();
    return 0;
}
