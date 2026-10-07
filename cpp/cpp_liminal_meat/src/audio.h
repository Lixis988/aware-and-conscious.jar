#pragma once

#include <string>
#include <memory>
#include <unordered_map>

struct ma_engine;
struct ma_sound;

class AudioSystem {
public:
    AudioSystem();
    ~AudioSystem();

    bool init();
    void shutdown();

    bool load_sound(const std::string& name, const std::string& filepath, bool loop = false);
    void play_sound(const std::string& name);
    void stop_sound(const std::string& name);
    void set_volume(const std::string& name, float volume);

    bool play_music(const std::string& filepath, float volume = 0.5f);
    void stop_music();
    void set_music_volume(float volume);

    void set_master_volume(float volume);
    void pause_all();
    void resume_all();

private:
    ma_engine* engine = nullptr;
    ma_sound* music = nullptr;

    struct SoundEntry {
        ma_sound* sound = nullptr;
        bool looping = false;
    };
    std::unordered_map<std::string, SoundEntry> sounds;

    bool initialized = false;
};

std::string resolve_audio_path(const std::string& filename);
