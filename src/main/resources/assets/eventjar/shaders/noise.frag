#version 330 core

// Текстурный сэмплер, используется для получения цвета пикселя
uniform sampler2D DiffuseSampler;

// Время, передаваемое из Minecraft (в секундах)
uniform float time;

// Интенсивность наложения шума (0.0 - без шума, 1.0 - полностью шум)
uniform float noiseIntensity;

// Входные координаты текстуры
in vec2 texCoord0;

// Выходной цвет фрагмента
out vec4 fragColor;

// Функция генерации псевдослучайного числа на основе координат
float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    // Получаем исходный цвет из текстуры
    vec4 color = texture(DiffuseSampler, texCoord0);
    
    // Генерируем шум, масштабируя координаты на значение времени
    float noise = rand(texCoord0 * time);
    
    // Смешиваем исходный цвет с шумом, используя заданную интенсивность
    fragColor = mix(color, vec4(vec3(noise), 1.0), noiseIntensity);
}
