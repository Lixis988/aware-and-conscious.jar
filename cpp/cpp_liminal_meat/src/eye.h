#pragma once

#include <GL/glew.h>
#include <string>
#include <vector>

#include "math_utils.h"
#include "shader.h"

class LiminalSpace;

class Eye {
public:

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;

    float size = 160.0f;
    float radius = 30.0f;
    float speed = 150.0f;
    float catch_distance = 95.0f;
    bool caught = false;

    Eye() = default;
    ~Eye();

    bool load(const std::string& gif_filename);

    void reset(const LiminalSpace& space, float from_x, float from_z, float floor_y);
    void update(float dt, float player_x, float player_z, const LiminalSpace& space);
    void render(const Mat4& view_proj, float cam_x, float cam_y, float cam_z);
    void destroy();

private:
    std::vector<GLuint> frames;
    std::vector<float> frame_delays;
    float total_duration = 1.0f;
    float anim_time = 0.0f;

    GLuint vao = 0;
    GLuint vbo = 0;
    Shader shader;
    GLint loc_mvp = -1;
    GLint loc_tex = -1;
    GLint loc_fog_color = -1;
    GLint loc_fog_density = -1;

    GLuint current_frame_tex() const;
};
