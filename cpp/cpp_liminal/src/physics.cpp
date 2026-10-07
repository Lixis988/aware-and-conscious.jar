#include "physics.h"
#include <random>

namespace PhysicsUtils {

static std::mt19937 rng(std::random_device{}());

float random_float(float min, float max) {
    std::uniform_real_distribution<float> dist(min, max);
    return dist(rng);
}

Vec3 random_vec3(float min, float max) {
    return Vec3(
        random_float(min, max),
        random_float(min, max),
        random_float(min, max)
    );
}

std::shared_ptr<SphereObject> create_random_ball(float y_min, float y_max, float tunnel_half_size) {
    auto ball = std::make_shared<SphereObject>(
        Vec3(
            random_float(-tunnel_half_size * 0.5f, tunnel_half_size * 0.5f),
            random_float(y_min, y_max),
            random_float(100.0f, 500.0f)
        ),
        random_float(0.5f, 2.0f),
        random_float(5.0f, 15.0f),
        false
    );

    ball->velocity = Vec3(
        random_float(-20.0f, 20.0f),
        random_float(-10.0f, 10.0f),
        random_float(-20.0f, 20.0f)
    );

    ball->restitution = random_float(0.4f, 0.8f);
    ball->friction = random_float(0.1f, 0.4f);

    return ball;
}

std::shared_ptr<BoxObject> create_floor(float tunnel_half_size, float tunnel_length) {
    auto floor = std::make_shared<BoxObject>(
        Vec3(0.0f, -tunnel_half_size - 5.0f, tunnel_length * 0.5f),
        Vec3(tunnel_half_size * 2.0f, 10.0f, tunnel_length),
        0.0f,
        true
    );
    floor->restitution = 0.3f;
    floor->friction = 0.5f;
    return floor;
}

}
