#version 150

uniform sampler2D DiffuseSampler;
uniform vec4 ColorModulator;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord) * ColorModulator;

    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));

    if (brightness < 0.3) {
        color.rgb = vec3(0.0);
    }

    fragColor = color;
}
