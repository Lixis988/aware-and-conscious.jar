#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float NoiseIntensity;

in vec2 texCoord0;
out vec4 fragColor;

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord0);
    float noise = rand(texCoord0 * max(Time, 0.001));
    float intensity = clamp(NoiseIntensity, 0.0, 1.0);
    fragColor = mix(color, vec4(vec3(noise), 1.0), intensity);
}
