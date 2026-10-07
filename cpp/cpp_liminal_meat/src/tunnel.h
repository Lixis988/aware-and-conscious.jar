#pragma once

#include <GL/glew.h>
#include <vector>

struct TunnelConfig {
    float half_size = 150.0f;
    float length = 12000.0f;
    float texture_repeat = 40.0f;
};

class Tunnel {
public:
    GLuint vao = 0;
    GLuint vbo = 0;
    int vertex_count = 0;

    TunnelConfig config;

    Tunnel() = default;
    ~Tunnel();

    void generate();
    void draw() const;

private:
    std::vector<float> vertices;

    void push_vertex(float x, float y, float z, float u, float v);
};
