#include "shader.h"
#include <iostream>

Shader::~Shader() {
    if (program) {
        glDeleteProgram(program);
    }
}

GLuint Shader::compile(GLenum type, const char* src) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &src, nullptr);
    glCompileShader(shader);

    GLint ok = 0;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &ok);
    if (!ok) {
        GLint len = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &len);
        std::string log(len, '\0');
        glGetShaderInfoLog(shader, len, nullptr, log.data());
        std::cerr << "Shader compile error: " << log << std::endl;
    }
    return shader;
}

bool Shader::load(const char* vertex_src, const char* fragment_src) {
    GLuint v = compile(GL_VERTEX_SHADER, vertex_src);
    GLuint f = compile(GL_FRAGMENT_SHADER, fragment_src);

    program = glCreateProgram();
    glAttachShader(program, v);
    glAttachShader(program, f);
    glLinkProgram(program);

    GLint ok = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &ok);
    if (!ok) {
        GLint len = 0;
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &len);
        std::string log(len, '\0');
        glGetProgramInfoLog(program, len, nullptr, log.data());
        std::cerr << "Program link error: " << log << std::endl;
        return false;
    }

    glDeleteShader(v);
    glDeleteShader(f);
    return true;
}

void Shader::use() const {
    glUseProgram(program);
}

GLint Shader::get_uniform(const char* name) const {
    return glGetUniformLocation(program, name);
}

void Shader::set_mat4(GLint loc, const float* data) const {
    glUniformMatrix4fv(loc, 1, GL_FALSE, data);
}

void Shader::set_float(GLint loc, float value) const {
    glUniform1f(loc, value);
}

void Shader::set_vec3(GLint loc, float x, float y, float z) const {
    glUniform3f(loc, x, y, z);
}

void Shader::set_int(GLint loc, int value) const {
    glUniform1i(loc, value);
}

const char* TUNNEL_VERT_SRC = R"(
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

const char* TUNNEL_FRAG_SRC = R"(
#version 330 core
in vec2 vUV;
in float vDepth;
out vec4 FragColor;

uniform sampler2D uTex;
uniform vec3 uFogColor;
uniform float uFogDensity;
uniform float uPaletteSteps;
uniform float uDitherStrength;

void main() {
    vec3 color = texture(uTex, vUV).rgb;

    float fogFactor = clamp(vDepth * uFogDensity, 0.0, 0.85);
    color = mix(color, uFogColor, fogFactor);

    vec2 noiseSeed = gl_FragCoord.xy * vec2(12.9898, 78.233);
    float noise = fract(sin(dot(noiseSeed, vec2(0.5, 0.25))) * 43758.5453);
    color += (noise - 0.5) * uDitherStrength;
    color = floor(color * uPaletteSteps) / uPaletteSteps;

    FragColor = vec4(color, 1.0);
}
)";
