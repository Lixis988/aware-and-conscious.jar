#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

void main() {

    float tile = 8.0;
    vec2 px = texCoord * OutSize;
    vec2 tileId = floor(px / tile);
    vec2 local = fract(px / tile);

    float stepT = floor(Time * 9.0);
    vec2 rnd = hash22(tileId + stepT * 3.17);
    float glitch = step(0.55, hash12(tileId * 1.7 + stepT));

    vec2 srcTile = tileId + floor((rnd - 0.5) * 10.0) * glitch;
    vec2 sampleUv = clamp((srcTile * tile + local * tile) / OutSize, 0.0, 1.0);

    vec4 color = texture(DiffuseSampler, sampleUv);

    if (glitch > 0.5) {
        if (rnd.y > 0.66) {
            color.rgb = color.brg;
        } else if (rnd.y > 0.33) {
            color.rgb = color.gbr;
        }
    }

    fragColor = vec4(color.rgb, 1.0);
}
