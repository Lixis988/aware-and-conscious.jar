#version 150

in vec4 position;
in vec2 texCoord;

out vec2 texCoordInterp;

void main() {
    texCoordInterp = texCoord;
    gl_Position = position;
}
