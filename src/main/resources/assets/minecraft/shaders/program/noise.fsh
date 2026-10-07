#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

float hash13(vec3 p3) {
    p3 = fract(p3 * 0.1031);
    p3 += dot(p3, p3.zyx + 31.32);
    return fract((p3.x + p3.y) * p3.z);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    vec2 cell = floor(gl_FragCoord.xy);

    float frame = mod(Time * 60.0, 1024.0);

    float n = hash13(vec3(cell, frame)) - 0.5;

    color.rgb = clamp(color.rgb + n * Intensity, 0.0, 1.0);

    fragColor = vec4(color.rgb, 1.0);
}
