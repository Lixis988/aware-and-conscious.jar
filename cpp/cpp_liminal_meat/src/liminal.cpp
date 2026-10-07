#include "liminal.h"

#include <algorithm>
#include <cmath>
#include <random>

LiminalSpace::LiminalSpace() {
    grid.assign(config.cells_z * config.cells_x, 0);
}

LiminalSpace::~LiminalSpace() {
    if (vbo) glDeleteBuffers(1, &vbo);
    if (vao) glDeleteVertexArrays(1, &vao);
}

int LiminalSpace::index(int r, int c) const {
    return r * config.cells_x + c;
}

bool LiminalSpace::in_bounds(int r, int c) const {
    return r >= 0 && c >= 0 && r < config.cells_z && c < config.cells_x;
}

void LiminalSpace::punch_rooms(std::mt19937& rng) {

    std::uniform_int_distribution<int> run_count(8, 16);
    std::uniform_int_distribution<int> run_len(4, 12);
    std::bernoulli_distribution horizontal(0.5);

    const int max_r = std::max(1, config.cells_z - 4);
    const int max_c = std::max(1, config.cells_x - 4);

    int runs = run_count(rng);
    for (int i = 0; i < runs; ++i) {
        bool horiz = horizontal(rng);
        int length = run_len(rng);
        int r0 = 2 + static_cast<int>(rng() % static_cast<unsigned>(max_r));
        int c0 = 2 + static_cast<int>(rng() % static_cast<unsigned>(max_c));
        int door = static_cast<int>(rng() % static_cast<unsigned>(std::max(1, length)));

        for (int k = 0; k < length; ++k) {
            int r = horiz ? r0 : r0 + k;
            int c = horiz ? c0 + k : c0;
            if (!in_bounds(r, c)) break;
            if (r <= 0 || c <= 0 || r >= config.cells_z - 1 || c >= config.cells_x - 1) continue;
            if (k == door || k == door + 1) continue;
            grid[index(r, c)] = 0;
        }
    }
}

void LiminalSpace::carve_maze() {
    std::mt19937 rng(std::random_device{}());

    grid.assign(config.cells_z * config.cells_x, 1);
    for (int c = 0; c < config.cells_x; ++c) {
        grid[index(0, c)] = 0;
        grid[index(config.cells_z - 1, c)] = 0;
    }
    for (int r = 0; r < config.cells_z; ++r) {
        grid[index(r, 0)] = 0;
        grid[index(r, config.cells_x - 1)] = 0;
    }

    std::bernoulli_distribution keep_pillar(0.85);
    std::bernoulli_distribution fat_pillar(0.30);
    for (int r = 2; r < config.cells_z - 2; r += 3) {
        for (int c = 2; c < config.cells_x - 2; c += 3) {
            if (!keep_pillar(rng)) continue;
            grid[index(r, c)] = 0;
            if (fat_pillar(rng)) {
                bool along_z = (rng() & 1u) != 0u;
                int nr = r + (along_z ? 1 : 0);
                int nc = c + (along_z ? 0 : 1);
                if (in_bounds(nr, nc)) grid[index(nr, nc)] = 0;
            }
        }
    }

    punch_rooms(rng);

    add_boundary_gates(rng);

    int sr = config.cells_z / 2;
    int sc = config.cells_x / 2;
    for (int r = sr - 2; r <= sr + 2; ++r) {
        for (int c = sc - 2; c <= sc + 2; ++c) {
            if (!in_bounds(r, c)) continue;
            if (r <= 0 || c <= 0 || r >= config.cells_z - 1 || c >= config.cells_x - 1) continue;
            grid[index(r, c)] = 1;
        }
    }
}

void LiminalSpace::add_boundary_gates(std::mt19937& rng) {

    std::uniform_int_distribution<int> gates_per_edge(2, 4);
    auto open_edge = [&](int edge) {
        int gates = gates_per_edge(rng);
        for (int i = 0; i < gates; ++i) {
            int pos = 1 + (rng() % (edge % 2 == 0 ? config.cells_x - 2 : config.cells_z - 2));
            if (edge == 0) grid[index(0, pos)] = 1;
            if (edge == 1) grid[index(config.cells_z - 1, pos)] = 1;
            if (edge == 2) grid[index(pos, 0)] = 1;
            if (edge == 3) grid[index(pos, config.cells_x - 1)] = 1;
        }
    };
    open_edge(0);
    open_edge(1);
    open_edge(2);
    open_edge(3);
}

void LiminalSpace::push_vertex(float x, float y, float z, float u, float v) {
    vertices.insert(vertices.end(), {x, y, z, u, v});
}

void LiminalSpace::push_quad(const Vec3& a, const Vec3& b, const Vec3& c, const Vec3& d,
                   float u0, float v0, float u1, float v1) {
    push_vertex(a.x, a.y, a.z, u0, v0);
    push_vertex(b.x, b.y, b.z, u1, v0);
    push_vertex(c.x, c.y, c.z, u1, v1);

    push_vertex(a.x, a.y, a.z, u0, v0);
    push_vertex(c.x, c.y, c.z, u1, v1);
    push_vertex(d.x, d.y, d.z, u0, v1);
}

void LiminalSpace::add_wall_block(int row, int col) {
    float half_h = config.half_height;
    float cell = config.cell_size;
    float hx = cell * 0.5f;
    float hz = cell * 0.5f;
    float cx = (-world_width * 0.5f) + (col + 0.5f) * cell;
    float cz = (row + 0.5f) * cell;
    float y0 = -half_h;
    float y1 = half_h;
    float u_span = config.texture_repeat;
    float v_span = config.texture_repeat;

    Vec3 blf{cx - hx, y0, cz - hz};
    Vec3 brf{cx + hx, y0, cz - hz};
    Vec3 trf{cx + hx, y1, cz - hz};
    Vec3 tlf{cx - hx, y1, cz - hz};

    Vec3 blb{cx - hx, y0, cz + hz};
    Vec3 brb{cx + hx, y0, cz + hz};
    Vec3 trb{cx + hx, y1, cz + hz};
    Vec3 tlb{cx - hx, y1, cz + hz};

    push_quad(blf, brf, trf, tlf, 0.0f, 0.0f, u_span, v_span);
    push_quad(brb, blb, tlb, trb, 0.0f, 0.0f, u_span, v_span);
    push_quad(blb, blf, tlf, tlb, 0.0f, 0.0f, u_span, v_span);
    push_quad(brf, brb, trb, trf, 0.0f, 0.0f, u_span, v_span);
    push_quad(tlf, trf, trb, tlb, 0.0f, 0.0f, u_span, u_span);
    push_quad(blf, brf, brb, blb, 0.0f, 0.0f, u_span, u_span);
}

void LiminalSpace::generate() {
    vertices.clear();

    carve_maze();

    world_width = config.cells_x * config.cell_size;
    world_length = config.cells_z * config.cell_size;

    constexpr float kSurfaceEps = 0.1f;
    float half_h = config.half_height;
    float floor_y = -half_h - kSurfaceEps;
    float ceil_y =  half_h + kSurfaceEps;
    float half_w = world_width * 0.5f;
    float len = world_length;

    float u_floor = (world_width / config.cell_size) * config.texture_repeat;
    float v_floor = (world_length / config.cell_size) * config.texture_repeat;

    Vec3 f0{-half_w, floor_y, 0.0f};
    Vec3 f1{ half_w, floor_y, 0.0f};
    Vec3 f2{ half_w, floor_y, len};
    Vec3 f3{-half_w, floor_y, len};
    push_quad(f0, f1, f2, f3, 0.0f, 0.0f, u_floor, v_floor);

    Vec3 c0{-half_w, ceil_y, len};
    Vec3 c1{ half_w, ceil_y, len};
    Vec3 c2{ half_w, ceil_y, 0.0f};
    Vec3 c3{-half_w, ceil_y, 0.0f};
    push_quad(c0, c1, c2, c3, 0.0f, 0.0f, u_floor, v_floor);

    for (int r = 0; r < config.cells_z; ++r) {
        for (int c = 0; c < config.cells_x; ++c) {
            if (grid[index(r,c)] == 0) {
                add_wall_block(r, c);
            }
        }
    }

    pick_spawn();

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

void LiminalSpace::draw() const {
    glBindVertexArray(vao);
    glDrawArrays(GL_TRIANGLES, 0, vertex_count);
}

bool LiminalSpace::is_wall_at(float world_x, float world_z) const {
    if (grid.empty()) return false;
    float half_w = world_width * 0.5f;

    int col = static_cast<int>(std::floor((world_x + half_w) / config.cell_size));
    int row = static_cast<int>(std::floor(world_z / config.cell_size));
    if (row < 0 || col < 0) return true;
    if (row >= config.cells_z || col >= config.cells_x) return true;
    return grid[index(row, col)] == 0;
}

void LiminalSpace::resolve_collision(float& x, float& z, float radius) const {
    if (grid.empty()) return;

    float half_w = world_width * 0.5f;
    float min_x = -half_w + radius;
    float max_x = half_w - radius;
    float min_z = radius;
    float max_z = world_length - radius;
    x = std::clamp(x, min_x, max_x);
    z = std::clamp(z, min_z, max_z);

    float inv_cell = 1.0f / config.cell_size;
    int min_col = static_cast<int>(std::floor((x - radius + half_w) * inv_cell));
    int max_col = static_cast<int>(std::floor((x + radius + half_w) * inv_cell));
    int min_row = static_cast<int>(std::floor((z - radius) * inv_cell));
    int max_row = static_cast<int>(std::floor((z + radius) * inv_cell));

    for (int r = min_row; r <= max_row; ++r) {
        for (int c = min_col; c <= max_col; ++c) {
            if (r < 0 || c < 0) continue;
            if (r >= config.cells_z || c >= config.cells_x) continue;
            if (grid[index(r, c)] != 0) continue;

            float cx = (-half_w) + (c + 0.5f) * config.cell_size;
            float cz = (r + 0.5f) * config.cell_size;
            float hx = config.cell_size * 0.5f;
            float hz = config.cell_size * 0.5f;

            float dx = x - cx;
            float dz = z - cz;
            float pen_x = (hx + radius) - std::abs(dx);
            float pen_z = (hz + radius) - std::abs(dz);
            if (pen_x > 0.0f && pen_z > 0.0f) {
                if (pen_x < pen_z) {
                    x += (dx > 0.0f ? pen_x : -pen_x);
                } else {
                    z += (dz > 0.0f ? pen_z : -pen_z);
                }
            }
        }
    }
}

void LiminalSpace::pick_spawn() {

    int best_r = -1;
    int best_c = -1;
    float center_r = static_cast<float>(config.cells_z) * 0.5f;
    float center_c = static_cast<float>(config.cells_x) * 0.5f;
    float best_score = 1e9f;

    for (int r = 0; r < config.cells_z; ++r) {
        for (int c = 0; c < config.cells_x; ++c) {
            if (grid[index(r, c)] == 0) continue;
            float dr = static_cast<float>(r) - center_r;
            float dc = static_cast<float>(c) - center_c;
            float score = std::abs(dr) + std::abs(dc);
            if (score < best_score) {
                best_score = score;
                best_r = r;
                best_c = c;
            }
        }
    }

    if (best_r < 0) {
        spawn_x = 0.0f;
        spawn_z = config.cell_size * 0.5f;
        return;
    }

    float half_w = world_width * 0.5f;
    spawn_x = (-half_w) + (best_c + 0.5f) * config.cell_size;
    spawn_z = (best_r + 0.5f) * config.cell_size;
}
