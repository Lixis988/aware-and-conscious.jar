#version 150

uniform sampler2D DiffuseSampler;
uniform vec4 ColorModulator;

in vec2 texCoordInterp;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoordInterp) * ColorModulator;
    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    
    // Измените порог по необходимости для усиления эффекта
    if (brightness < 0.01) {
        color.rgb = vec3(0.0); // Полностью чёрный
    }
    
    fragColor = color;
}
