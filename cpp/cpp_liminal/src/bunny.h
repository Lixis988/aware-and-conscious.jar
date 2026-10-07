#pragma once

#include <GL/glew.h>
#include <string>
#include <vector>
#include <random>

#include "math_utils.h"
#include "shader.h"

class LiminalSpace;

struct Bunny {
    float x = 0.0f, y = 0.0f, z = 0.0f;
    float vx = 0.0f, vz = 0.0f;
    float wander_timer = 0.0f;
    bool alive = true;
};

struct Gib {
    float x, y, z;
    float vx, vy, vz;
    float life;
    int tex_index;
};

class BunnyHunt {
public:
    float size = 90.0f;
    float radius = 40.0f;
    float speed = 220.0f;
    float melee_range = 160.0f;
    int kills = 0;
    static constexpr int kWinKills = 5;

    BunnyHunt() = default;
    ~BunnyHunt();

    bool load(const std::string& bunny_png);
    void spawn(const LiminalSpace& space, float from_x, float from_z, float floor_y, int count);
    void update(float dt, float player_x, float player_z, const LiminalSpace& space);

    bool try_hammer(float player_x, float player_y, float player_z, float yaw);
    void render(const Mat4& view_proj, float cam_x, float cam_y, float cam_z, float exposure);
    void destroy();

    int alive_count() const;

private:
    GLuint bunny_tex = 0;
    std::vector<GLuint> particle_tex;
    std::vector<Bunny> bunnies;
    std::vector<Gib> gibs;
    float floor_y = 0.0f;

    GLuint vao = 0;
    GLuint vbo = 0;
    Shader shader;
    GLint loc_mvp = -1;
    GLint loc_tex = -1;
    GLint loc_fog_color = -1;
    GLint loc_fog_density = -1;
    GLint loc_exposure = -1;

    std::mt19937 rng{std::random_device{}()};

    void spawn_gibs(float x, float y, float z);
    void draw_billboard(GLuint tex, float x, float y, float z, float half,
                        const Mat4& view_proj, float cam_x, float cam_z, float exposure, float fog);
};
