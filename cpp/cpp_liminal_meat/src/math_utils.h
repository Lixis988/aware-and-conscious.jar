#pragma once

#include <cmath>
#include <algorithm>

struct Mat4 {
    float m[16] = {};
};

inline Mat4 mat4_identity() {
    Mat4 r{};
    r.m[0] = r.m[5] = r.m[10] = r.m[15] = 1.0f;
    return r;
}

inline Mat4 mat4_perspective(float fov_rad, float aspect, float znear, float zfar) {
    Mat4 r{};
    float t = std::tan(fov_rad / 2.0f);
    r.m[0] = 1.0f / (aspect * t);
    r.m[5] = 1.0f / t;
    r.m[10] = -(zfar + znear) / (zfar - znear);
    r.m[11] = -1.0f;
    r.m[14] = -(2.0f * zfar * znear) / (zfar - znear);
    return r;
}

inline Mat4 mat4_look_at(const float eye[3], const float target[3], const float up[3]) {
    float f[3] = {target[0] - eye[0], target[1] - eye[1], target[2] - eye[2]};
    float flen = std::sqrt(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
    if (flen < 1e-6f) flen = 1.0f;
    f[0] /= flen; f[1] /= flen; f[2] /= flen;

    float s[3] = {
        f[1] * up[2] - f[2] * up[1],
        f[2] * up[0] - f[0] * up[2],
        f[0] * up[1] - f[1] * up[0]
    };
    float slen = std::sqrt(s[0] * s[0] + s[1] * s[1] + s[2] * s[2]);
    if (slen < 1e-6f) slen = 1.0f;
    s[0] /= slen; s[1] /= slen; s[2] /= slen;

    float u[3] = {
        s[1] * f[2] - s[2] * f[1],
        s[2] * f[0] - s[0] * f[2],
        s[0] * f[1] - s[1] * f[0]
    };

    Mat4 r = mat4_identity();
    r.m[0] = s[0]; r.m[4] = s[1]; r.m[8] = s[2];
    r.m[1] = u[0]; r.m[5] = u[1]; r.m[9] = u[2];
    r.m[2] = -f[0]; r.m[6] = -f[1]; r.m[10] = -f[2];
    r.m[12] = -(s[0] * eye[0] + s[1] * eye[1] + s[2] * eye[2]);
    r.m[13] = -(u[0] * eye[0] + u[1] * eye[1] + u[2] * eye[2]);
    r.m[14] = f[0] * eye[0] + f[1] * eye[1] + f[2] * eye[2];
    return r;
}

inline Mat4 mat4_mul(const Mat4 &a, const Mat4 &b) {
    Mat4 r{};
    for (int col = 0; col < 4; ++col) {
        for (int row = 0; row < 4; ++row) {
            float sum = 0.0f;
            for (int k = 0; k < 4; ++k) {
                sum += a.m[k * 4 + row] * b.m[col * 4 + k];
            }
            r.m[col * 4 + row] = sum;
        }
    }
    return r;
}

constexpr float PI = 3.14159265358979323846f;
constexpr float DEG_TO_RAD = PI / 180.0f;
