#pragma once

#include <GL/glew.h>
#include <string>
#include <random>
#include <vector>

struct LiminalConfig {

    float half_height = 150.0f;
    float cell_size = 180.0f;
    int cells_x = 40;
    int cells_z = 28;
    float texture_repeat = 2.2f;
    std::string texture_name = "texture2.jpg";
};

class LiminalSpace {
public:
    GLuint vao = 0;
    GLuint vbo = 0;
    int vertex_count = 0;

    LiminalConfig config;
    float world_width = 0.0f;
    float world_length = 0.0f;
    float spawn_x = 0.0f;
    float spawn_z = 0.0f;

    LiminalSpace();
    ~LiminalSpace();

    void generate();
    void draw() const;
    void resolve_collision(float& x, float& z, float radius) const;
    bool is_wall_at(float world_x, float world_z) const;

private:
    struct Vec3 { float x, y, z; };

    std::vector<unsigned char> grid;
    std::vector<float> vertices;
    int index(int r, int c) const;

    void push_vertex(float x, float y, float z, float u, float v);
    void push_quad(const Vec3& a, const Vec3& b, const Vec3& c, const Vec3& d,
                   float u0, float v0, float u1, float v1);
    void add_wall_block(int row, int col);
    void carve_maze();
    void punch_rooms(std::mt19937& rng);
    void add_boundary_gates(std::mt19937& rng);
    void pick_spawn();
    bool in_bounds(int r, int c) const;
};
