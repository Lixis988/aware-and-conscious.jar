#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    vec3 inverted = 1.0 - color.rgb;
    vec3 result = mix(color.rgb, inverted, clamp(Intensity, 0.0, 1.0));
    fragColor = vec4(result, 1.0);
}
