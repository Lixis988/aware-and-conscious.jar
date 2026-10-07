#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

void main() {
    vec2 uv = texCoord;
    float t = Time;

    float wave = sin((uv.y + t * 0.6) * 18.0) * 0.004
               + sin((uv.y - t * 0.35) * 47.0) * 0.0015;
    vec2 dUv = clamp(uv + vec2(wave * Intensity, 0.0), 0.0, 1.0);

    float ca = (0.004 + 0.012 * Intensity) * (0.7 + 0.3 * sin(t * 2.3));
    float r = texture(DiffuseSampler, clamp(dUv + vec2(ca, 0.0), 0.0, 1.0)).r;
    float g = texture(DiffuseSampler, dUv).g;
    float b = texture(DiffuseSampler, clamp(dUv - vec2(ca, 0.0), 0.0, 1.0)).b;
    vec3 col = vec3(r, g, b);

    float swap = 0.5 + 0.5 * sin(t * 1.7);
    col = mix(col, col.gbr, swap * Intensity);

    float sat = 1.0 + 1.6 * Intensity;
    float luma = dot(col, vec3(0.299, 0.587, 0.114));
    col = clamp(mix(vec3(luma), col, sat), 0.0, 1.0);

    float flick = hash11(floor(t * 10.0));
    if (flick > 1.0 - 0.08 * Intensity) {
        col = mix(col, vec3(1.0, 0.0, 1.0), 0.5);
    }

    fragColor = vec4(col, 1.0);
}
