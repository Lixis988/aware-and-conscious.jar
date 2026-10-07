#pragma once

#include <GL/glew.h>
#include <string>

class Shader {
public:
    GLuint program = 0;

    Shader() = default;
    ~Shader();

    bool load(const char* vertex_src, const char* fragment_src);
    void use() const;

    GLint get_uniform(const char* name) const;

    void set_mat4(GLint loc, const float* data) const;
    void set_float(GLint loc, float value) const;
    void set_vec3(GLint loc, float x, float y, float z) const;
    void set_int(GLint loc, int value) const;

private:
    GLuint compile(GLenum type, const char* src);
};

extern const char* TUNNEL_VERT_SRC;
extern const char* TUNNEL_FRAG_SRC;
