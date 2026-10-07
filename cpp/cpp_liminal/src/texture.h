#pragma once

#include <GL/glew.h>
#include <string>

class Texture {
public:
    GLuint id = 0;
    int width = 0;
    int height = 0;

    Texture() = default;
    ~Texture();

    bool load(const std::string& filename);
    void bind(int unit = 0) const;

    void load_fallback();

private:
    void create_fallback();
};

std::string resolve_asset_path(const std::string& filename);
