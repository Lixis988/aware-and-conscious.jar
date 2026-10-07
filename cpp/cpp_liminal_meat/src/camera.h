#pragma once

#include "math_utils.h"
#include <GLFW/glfw3.h>

class Camera {
public:

    float x = 0.0f;
    float y = 0.0f;
    float z = 50.0f;

    float yaw = 0.0f;
    float pitch = 0.0f;

    float move_speed = 150.0f;
    float run_multiplier = 2.2f;
    float mouse_sensitivity = 0.002f;

    float velocity_y = 0.0f;
    float gravity = -500.0f;
    float jump_force = 200.0f;
    float player_height = 30.0f;
    bool on_ground = false;
    bool walking_enabled = true;

    float floor_y = -150.0f;

    Camera() = default;

    void process_input(GLFWwindow* window, float dt);

    Mat4 get_view_matrix() const;

    void get_forward(float out[3]) const;
    void get_right(float out[3]) const;

    void set_floor(float floor_level) {
        floor_y = floor_level;
    }

private:
    double last_mouse_x = 0.0;
    double last_mouse_y = 0.0;
    bool first_mouse = true;

    void apply_gravity(float dt);
    void check_ground();
};
