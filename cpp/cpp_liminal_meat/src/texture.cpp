#include "texture.h"
#include "stb_image.h"
#include <filesystem>
#include <iostream>
#include <vector>

std::string resolve_asset_path(const std::string& filename) {
    namespace fs = std::filesystem;
    const fs::path file = filename;
    const fs::path cwd = fs::current_path();
    const fs::path source_root = fs::path(__FILE__).parent_path().parent_path().parent_path();

    std::vector<fs::path> candidates;

    for (int up = 0; up <= 5; ++up) {
        fs::path base = cwd;
        for (int i = 0; i < up; ++i) base = base.parent_path();
        candidates.push_back(base / "assets" / file);
    }

    candidates.push_back(source_root / "assets" / file);

    for (const auto& p : candidates) {
        std::error_code ec;
        if (fs::exists(p, ec) && fs::is_regular_file(p, ec)) {
            return p.string();
        }
    }

    return filename;
}

Texture::~Texture() {
    if (id) {
        glDeleteTextures(1, &id);
    }
}

void Texture::create_fallback() {
    width = height = 128;
    std::vector<unsigned char> data(width * height * 3);

    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            int c = ((x / 16) + (y / 16)) % 2 ? 220 : 50;
            data[(y * width + x) * 3 + 0] = static_cast<unsigned char>(c);
            data[(y * width + x) * 3 + 1] = static_cast<unsigned char>(c);
            data[(y * width + x) * 3 + 2] = static_cast<unsigned char>(c - 20);
        }
    }

    glGenTextures(1, &id);
    glBindTexture(GL_TEXTURE_2D, id);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data.data());
    glGenerateMipmap(GL_TEXTURE_2D);
}

bool Texture::load(const std::string& filename) {
    std::string path = resolve_asset_path(filename);

    stbi_set_flip_vertically_on_load(1);
    int channels;
    unsigned char* data = stbi_load(path.c_str(), &width, &height, &channels, 3);

    if (!data) {
        std::cerr << "Failed to load texture: " << path << "\nUsing checkerboard fallback." << std::endl;
        create_fallback();
        return false;
    }

    glGenTextures(1, &id);
    glBindTexture(GL_TEXTURE_2D, id);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
    glGenerateMipmap(GL_TEXTURE_2D);

    stbi_image_free(data);
    return true;
}

void Texture::load_fallback() {
    create_fallback();
}

void Texture::bind(int unit) const {
    glActiveTexture(GL_TEXTURE0 + unit);
    glBindTexture(GL_TEXTURE_2D, id);
}
