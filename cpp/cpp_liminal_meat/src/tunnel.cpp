#include "tunnel.h"

Tunnel::~Tunnel() {
    if (vbo) glDeleteBuffers(1, &vbo);
    if (vao) glDeleteVertexArrays(1, &vao);
}

void Tunnel::push_vertex(float x, float y, float z, float u, float v) {
    vertices.insert(vertices.end(), {x, y, z, u, v});
}

void Tunnel::generate() {
    vertices.clear();
    vertices.reserve(4 * 6 * 5);

    float hs = config.half_size;
    float len = config.length;
    float u0 = 0.0f, u1 = config.texture_repeat;
    float v0 = 0.0f, v1 = 1.0f;

    {
        float y = -hs;
        push_vertex(-hs, y, 0.0f, u0, v0);
        push_vertex(hs, y, 0.0f, u0, v1);
        push_vertex(hs, y, len, u1, v1);
        push_vertex(-hs, y, 0.0f, u0, v0);
        push_vertex(hs, y, len, u1, v1);
        push_vertex(-hs, y, len, u1, v0);
    }

    {
        float y = hs;
        push_vertex(-hs, y, 0.0f, u0, v0);
        push_vertex(-hs, y, len, u1, v0);
        push_vertex(hs, y, len, u1, v1);
        push_vertex(-hs, y, 0.0f, u0, v0);
        push_vertex(hs, y, len, u1, v1);
        push_vertex(hs, y, 0.0f, u0, v1);
    }

    {
        float x = -hs;
        push_vertex(x, -hs, 0.0f, u0, v0);
        push_vertex(x, -hs, len, u1, v0);
        push_vertex(x, hs, len, u1, v1);
        push_vertex(x, -hs, 0.0f, u0, v0);
        push_vertex(x, hs, len, u1, v1);
        push_vertex(x, hs, 0.0f, u0, v1);
    }

    {
        float x = hs;
        push_vertex(x, -hs, 0.0f, u0, v0);
        push_vertex(x, hs, 0.0f, u0, v1);
        push_vertex(x, hs, len, u1, v1);
        push_vertex(x, -hs, 0.0f, u0, v0);
        push_vertex(x, hs, len, u1, v1);
        push_vertex(x, -hs, len, u1, v0);
    }

    vertex_count = static_cast<int>(vertices.size() / 5);

    glGenVertexArrays(1, &vao);
    glGenBuffers(1, &vbo);

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices.size() * sizeof(float), vertices.data(), GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);
}

void Tunnel::draw() const {
    glBindVertexArray(vao);
    glDrawArrays(GL_TRIANGLES, 0, vertex_count);
}
