#pragma once

#include <vector>
#include <memory>
#include <cmath>
#include <algorithm>

struct Vec3 {
    float x = 0.0f, y = 0.0f, z = 0.0f;

    Vec3() = default;
    Vec3(float x_, float y_, float z_) : x(x_), y(y_), z(z_) {}

    Vec3 operator+(const Vec3& v) const { return {x + v.x, y + v.y, z + v.z}; }
    Vec3 operator-(const Vec3& v) const { return {x - v.x, y - v.y, z - v.z}; }
    Vec3 operator*(float s) const { return {x * s, y * s, z * s}; }
    Vec3 operator/(float s) const { return {x / s, y / s, z / s}; }

    Vec3& operator+=(const Vec3& v) { x += v.x; y += v.y; z += v.z; return *this; }
    Vec3& operator-=(const Vec3& v) { x -= v.x; y -= v.y; z -= v.z; return *this; }
    Vec3& operator*=(float s) { x *= s; y *= s; z *= s; return *this; }

    float dot(const Vec3& v) const { return x * v.x + y * v.y + z * v.z; }
    Vec3 cross(const Vec3& v) const {
        return {y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x};
    }

    float length() const { return std::sqrt(x * x + y * y + z * z); }
    float length_sq() const { return x * x + y * y + z * z; }

    Vec3 normalized() const {
        float len = length();
        if (len < 1e-6f) return {0, 0, 0};
        return *this / len;
    }
};

enum class PhysicsShape {
    Sphere,
    Box
};

class PhysicsObject {
public:
    Vec3 position;
    Vec3 velocity;
    Vec3 acceleration;

    float mass = 1.0f;
    float inv_mass = 1.0f;

    float restitution = 0.5f;
    float friction = 0.3f;

    bool is_static = false;
    bool is_active = true;

    PhysicsShape shape = PhysicsShape::Sphere;

    PhysicsObject() = default;
    virtual ~PhysicsObject() = default;

    void set_mass(float m) {
        mass = m;
        inv_mass = (m > 0.0f && !is_static) ? 1.0f / m : 0.0f;
    }

    void make_static() {
        is_static = true;
        inv_mass = 0.0f;
    }

    virtual void integrate(float dt) {
        if (is_static || !is_active) return;

        velocity += acceleration * dt;
        position += velocity * dt;
    }
};

class SphereObject : public PhysicsObject {
public:
    float radius = 0.5f;

    SphereObject() { shape = PhysicsShape::Sphere; }

    SphereObject(const Vec3& pos, float mass_, float radius_, bool static_ = false)
        : radius(radius_)
    {
        shape = PhysicsShape::Sphere;
        position = pos;
        set_mass(mass_);
        if (static_) make_static();
    }
};

class BoxObject : public PhysicsObject {
public:
    Vec3 half_size = {0.5f, 0.5f, 0.5f};

    BoxObject() { shape = PhysicsShape::Box; }

    BoxObject(const Vec3& pos, const Vec3& size, float mass_, bool static_ = false)
        : half_size(size * 0.5f)
    {
        shape = PhysicsShape::Box;
        position = pos;
        set_mass(mass_);
        if (static_) make_static();
    }

    Vec3 get_min() const { return position - half_size; }
    Vec3 get_max() const { return position + half_size; }
};

struct CollisionInfo {
    bool collided = false;
    Vec3 normal;
    float penetration = 0.0f;
    Vec3 contact_point;
};

class PhysicsWorld {
public:
    Vec3 gravity = {0.0f, -9.81f, 0.0f};

    float tunnel_half_size = 150.0f;
    float tunnel_length = 12000.0f;

    std::vector<std::shared_ptr<PhysicsObject>> objects;

    void set_tunnel_params(float half_size, float length) {
        tunnel_half_size = half_size;
        tunnel_length = length;
    }

    void add_object(std::shared_ptr<PhysicsObject> obj) {
        objects.push_back(obj);
    }

    void remove_object(std::shared_ptr<PhysicsObject> obj) {
        objects.erase(
            std::remove(objects.begin(), objects.end(), obj),
            objects.end()
        );
    }

    void clear() {
        objects.clear();
    }

    void update(float dt) {

        for (auto& obj : objects) {
            if (!obj->is_static && obj->is_active) {
                obj->acceleration = gravity;
            }
        }

        for (auto& obj : objects) {
            obj->integrate(dt);
        }

        resolve_collisions();

        constrain_to_tunnel();
    }

private:
    void resolve_collisions() {
        for (size_t i = 0; i < objects.size(); ++i) {
            for (size_t j = i + 1; j < objects.size(); ++j) {
                auto& a = objects[i];
                auto& b = objects[j];

                if (a->is_static && b->is_static) continue;

                CollisionInfo info = check_collision(a.get(), b.get());
                if (info.collided) {
                    resolve_collision(a.get(), b.get(), info);
                }
            }
        }
    }

    CollisionInfo check_collision(PhysicsObject* a, PhysicsObject* b) {

        if (a->shape == PhysicsShape::Sphere && b->shape == PhysicsShape::Sphere) {
            return sphere_vs_sphere(
                static_cast<SphereObject*>(a),
                static_cast<SphereObject*>(b)
            );
        }

        if (a->shape == PhysicsShape::Sphere && b->shape == PhysicsShape::Box) {
            return sphere_vs_box(
                static_cast<SphereObject*>(a),
                static_cast<BoxObject*>(b)
            );
        }
        if (a->shape == PhysicsShape::Box && b->shape == PhysicsShape::Sphere) {
            auto info = sphere_vs_box(
                static_cast<SphereObject*>(b),
                static_cast<BoxObject*>(a)
            );
            info.normal = info.normal * -1.0f;
            return info;
        }

        if (a->shape == PhysicsShape::Box && b->shape == PhysicsShape::Box) {
            return box_vs_box(
                static_cast<BoxObject*>(a),
                static_cast<BoxObject*>(b)
            );
        }

        return {};
    }

    CollisionInfo sphere_vs_sphere(SphereObject* a, SphereObject* b) {
        CollisionInfo info;

        Vec3 diff = b->position - a->position;
        float dist_sq = diff.length_sq();
        float radius_sum = a->radius + b->radius;

        if (dist_sq < radius_sum * radius_sum) {
            float dist = std::sqrt(dist_sq);
            info.collided = true;
            info.normal = dist > 0.001f ? diff / dist : Vec3(0, 1, 0);
            info.penetration = radius_sum - dist;
            info.contact_point = a->position + info.normal * a->radius;
        }

        return info;
    }

    CollisionInfo sphere_vs_box(SphereObject* sphere, BoxObject* box) {
        CollisionInfo info;

        Vec3 closest;
        closest.x = std::clamp(sphere->position.x, box->get_min().x, box->get_max().x);
        closest.y = std::clamp(sphere->position.y, box->get_min().y, box->get_max().y);
        closest.z = std::clamp(sphere->position.z, box->get_min().z, box->get_max().z);

        Vec3 diff = sphere->position - closest;
        float dist_sq = diff.length_sq();

        if (dist_sq < sphere->radius * sphere->radius) {
            float dist = std::sqrt(dist_sq);
            info.collided = true;
            info.normal = dist > 0.001f ? diff / dist : Vec3(0, 1, 0);
            info.penetration = sphere->radius - dist;
            info.contact_point = closest;
        }

        return info;
    }

    CollisionInfo box_vs_box(BoxObject* a, BoxObject* b) {
        CollisionInfo info;

        Vec3 a_min = a->get_min(), a_max = a->get_max();
        Vec3 b_min = b->get_min(), b_max = b->get_max();

        float overlap_x = std::min(a_max.x, b_max.x) - std::max(a_min.x, b_min.x);
        float overlap_y = std::min(a_max.y, b_max.y) - std::max(a_min.y, b_min.y);
        float overlap_z = std::min(a_max.z, b_max.z) - std::max(a_min.z, b_min.z);

        if (overlap_x > 0 && overlap_y > 0 && overlap_z > 0) {
            info.collided = true;

            if (overlap_x <= overlap_y && overlap_x <= overlap_z) {
                info.penetration = overlap_x;
                info.normal = a->position.x < b->position.x ? Vec3(-1, 0, 0) : Vec3(1, 0, 0);
            } else if (overlap_y <= overlap_z) {
                info.penetration = overlap_y;
                info.normal = a->position.y < b->position.y ? Vec3(0, -1, 0) : Vec3(0, 1, 0);
            } else {
                info.penetration = overlap_z;
                info.normal = a->position.z < b->position.z ? Vec3(0, 0, -1) : Vec3(0, 0, 1);
            }
        }

        return info;
    }

    void resolve_collision(PhysicsObject* a, PhysicsObject* b, const CollisionInfo& info) {

        float total_inv_mass = a->inv_mass + b->inv_mass;
        if (total_inv_mass <= 0.0f) return;

        Vec3 correction = info.normal * (info.penetration / total_inv_mass) * 0.8f;
        a->position -= correction * a->inv_mass;
        b->position += correction * b->inv_mass;

        Vec3 rel_vel = b->velocity - a->velocity;
        float vel_along_normal = rel_vel.dot(info.normal);

        if (vel_along_normal > 0) return;

        float e = std::min(a->restitution, b->restitution);

        float j = -(1.0f + e) * vel_along_normal / total_inv_mass;

        Vec3 impulse = info.normal * j;
        a->velocity -= impulse * a->inv_mass;
        b->velocity += impulse * b->inv_mass;

        Vec3 tangent = rel_vel - info.normal * vel_along_normal;
        float tangent_len = tangent.length();
        if (tangent_len > 0.001f) {
            tangent = tangent / tangent_len;

            float friction_coef = std::sqrt(a->friction * b->friction);
            float jt = -rel_vel.dot(tangent) / total_inv_mass;
            jt = std::clamp(jt, -j * friction_coef, j * friction_coef);

            Vec3 friction_impulse = tangent * jt;
            a->velocity -= friction_impulse * a->inv_mass;
            b->velocity += friction_impulse * b->inv_mass;
        }
    }

    void constrain_to_tunnel() {
        float limit = tunnel_half_size - 1.0f;

        for (auto& obj : objects) {
            if (obj->is_static) continue;

            float radius = 0.0f;
            if (obj->shape == PhysicsShape::Sphere) {
                radius = static_cast<SphereObject*>(obj.get())->radius;
            } else if (obj->shape == PhysicsShape::Box) {
                auto* box = static_cast<BoxObject*>(obj.get());
                radius = std::max({box->half_size.x, box->half_size.y, box->half_size.z});
            }

            float effective_limit = limit - radius;

            if (obj->position.x < -effective_limit) {
                obj->position.x = -effective_limit;
                obj->velocity.x = std::abs(obj->velocity.x) * obj->restitution;
            }
            if (obj->position.x > effective_limit) {
                obj->position.x = effective_limit;
                obj->velocity.x = -std::abs(obj->velocity.x) * obj->restitution;
            }

            if (obj->position.y < -effective_limit) {
                obj->position.y = -effective_limit;
                obj->velocity.y = std::abs(obj->velocity.y) * obj->restitution;

                obj->velocity.x *= (1.0f - obj->friction * 0.1f);
                obj->velocity.z *= (1.0f - obj->friction * 0.1f);
            }
            if (obj->position.y > effective_limit) {
                obj->position.y = effective_limit;
                obj->velocity.y = -std::abs(obj->velocity.y) * obj->restitution;
            }

            if (obj->position.z < radius) {
                obj->position.z = radius;
                obj->velocity.z = std::abs(obj->velocity.z) * obj->restitution;
            }
            if (obj->position.z > tunnel_length - radius) {
                obj->position.z = tunnel_length - radius;
                obj->velocity.z = -std::abs(obj->velocity.z) * obj->restitution;
            }
        }
    }
};
