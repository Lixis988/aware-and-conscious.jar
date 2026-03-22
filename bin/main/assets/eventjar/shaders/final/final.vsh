#version 150

// Позиция и координаты текстуры для каждого вертекса
in vec4 position;
in vec2 texCoord;

// Передаём координаты в фрагментный шейдер
out vec2 texCoordInterp;

void main() {
    texCoordInterp = texCoord;
    gl_Position = position;
}
