#version 150

uniform sampler2D DiffuseSampler;
uniform float Levels;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float levels = max(2.0, Levels);
    vec3 result = floor(color.rgb * levels + 0.5) / levels;
    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}
