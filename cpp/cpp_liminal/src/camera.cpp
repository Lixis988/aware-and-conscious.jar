#include "camera.h"
#include <algorithm>

void Camera::get_forward(float out[3]) const {

    out[0] = std::sin(yaw);
    out[1] = 0.0f;
    out[2] = std::cos(yaw);
}

void Camera::get_right(float out[3]) const {
    out[0] = std::sin(yaw + PI / 2.0f);
    out[1] = 0.0f;
    out[2] = std::cos(yaw + PI / 2.0f);
}

void Camera::apply_gravity(float dt) {
    if (!walking_enabled) return;

    if (!on_ground) {
        velocity_y += gravity * dt;
    }

    y += velocity_y * dt;

    check_ground();
}

void Camera::check_ground() {
    float ground_level = floor_y + player_height;

    if (y <= ground_level) {
        y = ground_level;
        velocity_y = 0.0f;
        on_ground = true;
    } else {
        on_ground = false;
    }
}

void Camera::process_input(GLFWwindow* window, float dt) {

    double mx, my;
    glfwGetCursorPos(window, &mx, &my);

    if (first_mouse) {
        last_mouse_x = mx;
        last_mouse_y = my;
        first_mouse = false;
    }

    float dx = static_cast<float>(mx - last_mouse_x);
    float dy = static_cast<float>(my - last_mouse_y);
    last_mouse_x = mx;
    last_mouse_y = my;

    yaw -= dx * mouse_sensitivity;
    pitch -= dy * mouse_sensitivity;

    const float max_pitch = 1.4f;
    pitch = std::clamp(pitch, -max_pitch, max_pitch);

    float forward[3], right[3];
    get_forward(forward);
    get_right(right);

    bool sprinting = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS ||
                     glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
    float velocity = move_speed * (sprinting ? run_multiplier : 1.0f) * dt;

    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
        x += forward[0] * velocity;
        z += forward[2] * velocity;
    }
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
        x -= forward[0] * velocity;
        z -= forward[2] * velocity;
    }

    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
        x += right[0] * velocity;
        z += right[2] * velocity;
    }
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
        x -= right[0] * velocity;
        z -= right[2] * velocity;
    }

    if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && on_ground && walking_enabled) {
        velocity_y = jump_force;
        on_ground = false;
    }

    apply_gravity(dt);
}

Mat4 Camera::get_view_matrix() const {

    float look_forward[3] = {
        std::sin(yaw) * std::cos(pitch),
        std::sin(pitch),
        std::cos(yaw) * std::cos(pitch)
    };

    float eye[3] = {x, y, z};
    float target[3] = {x + look_forward[0], y + look_forward[1], z + look_forward[2]};
    float up[3] = {0.0f, 1.0f, 0.0f};

    return mat4_look_at(eye, target, up);
}
