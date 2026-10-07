#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

void main() {
    vec4 c = texture(DiffuseSampler, texCoord);
    vec3 col = c.rgb;

    float seg = floor(Time / 1.5);
    float r = hash11(seg + 0.5);
    int mode = int(floor(r * 4.0));

    if (mode == 0) {

        col = 1.0 - col;
    } else if (mode == 1) {

        float levels = 3.0 + floor(hash11(seg + 1.3) * 4.0);
        col = floor(col * levels + 0.5) / levels;
    } else if (mode == 2) {

        col = (hash11(seg + 2.7) > 0.5) ? col.gbr : col.brg;
    } else {

        float luma = dot(col, vec3(0.299, 0.587, 0.114));
        float sat = 1.5 + hash11(seg + 3.9) * 2.0;
        col = clamp(mix(vec3(luma), col, sat), 0.0, 1.0);
    }

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}
