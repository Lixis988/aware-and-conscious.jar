#define MINIAUDIO_IMPLEMENTATION
#include "miniaudio.h"
#include "audio.h"

#include <filesystem>
#include <iostream>
#include <vector>

std::string resolve_audio_path(const std::string& filename) {
    namespace fs = std::filesystem;
    const fs::path file = filename;
    const fs::path cwd = fs::current_path();
    const fs::path source_root = fs::path(__FILE__).parent_path().parent_path().parent_path();

    std::vector<fs::path> candidates;

    for (int up = 0; up <= 5; ++up) {
        fs::path base = cwd;
        for (int i = 0; i < up; ++i) base = base.parent_path();
        candidates.push_back(base / "audio" / file);
        candidates.push_back(base / "audio" / "SFX" / file);
    }

    candidates.push_back(source_root / "audio" / file);
    candidates.push_back(source_root / "audio" / "SFX" / file);

    for (const auto& p : candidates) {
        std::error_code ec;
        if (fs::exists(p, ec) && fs::is_regular_file(p, ec)) {
            return p.string();
        }
    }

    return filename;
}

AudioSystem::AudioSystem() = default;

AudioSystem::~AudioSystem() {
    shutdown();
}

bool AudioSystem::init() {
    if (initialized) return true;

    engine = new ma_engine();

    ma_result result = ma_engine_init(nullptr, engine);
    if (result != MA_SUCCESS) {
        std::cerr << "Failed to initialize audio engine" << std::endl;
        delete engine;
        engine = nullptr;
        return false;
    }

    initialized = true;
    std::cout << "Audio system initialized" << std::endl;
    return true;
}

void AudioSystem::shutdown() {
    if (!initialized) return;

    for (auto& [name, entry] : sounds) {
        if (entry.sound) {
            ma_sound_uninit(entry.sound);
            delete entry.sound;
        }
    }
    sounds.clear();

    if (music) {
        ma_sound_uninit(music);
        delete music;
        music = nullptr;
    }

    if (engine) {
        ma_engine_uninit(engine);
        delete engine;
        engine = nullptr;
    }

    initialized = false;
}

bool AudioSystem::load_sound(const std::string& name, const std::string& filepath, bool loop) {
    if (!initialized) return false;

    std::string path = resolve_audio_path(filepath);

    auto* sound = new ma_sound();
    ma_uint32 flags = 0;

    ma_result result = ma_sound_init_from_file(engine, path.c_str(), flags, nullptr, nullptr, sound);
    if (result != MA_SUCCESS) {
        std::cerr << "Failed to load sound: " << path << std::endl;
        delete sound;
        return false;
    }

    if (loop) {
        ma_sound_set_looping(sound, MA_TRUE);
    }

    sounds[name] = {sound, loop};
    std::cout << "Loaded sound: " << name << " from " << path << std::endl;
    return true;
}

void AudioSystem::play_sound(const std::string& name) {
    auto it = sounds.find(name);
    if (it != sounds.end() && it->second.sound) {
        ma_sound_seek_to_pcm_frame(it->second.sound, 0);
        ma_sound_start(it->second.sound);
    }
}

void AudioSystem::stop_sound(const std::string& name) {
    auto it = sounds.find(name);
    if (it != sounds.end() && it->second.sound) {
        ma_sound_stop(it->second.sound);
    }
}

void AudioSystem::set_volume(const std::string& name, float volume) {
    auto it = sounds.find(name);
    if (it != sounds.end() && it->second.sound) {
        ma_sound_set_volume(it->second.sound, volume);
    }
}

bool AudioSystem::play_music(const std::string& filepath, float volume) {
    if (!initialized) return false;

    stop_music();

    std::string path = resolve_audio_path(filepath);

    music = new ma_sound();
    ma_uint32 flags = MA_SOUND_FLAG_STREAM;

    ma_result result = ma_sound_init_from_file(engine, path.c_str(), flags, nullptr, nullptr, music);
    if (result != MA_SUCCESS) {
        std::cerr << "Failed to load music: " << path << std::endl;
        delete music;
        music = nullptr;
        return false;
    }

    ma_sound_set_looping(music, MA_TRUE);
    ma_sound_set_volume(music, volume);
    ma_sound_start(music);

    std::cout << "Playing music: " << path << std::endl;
    return true;
}

void AudioSystem::stop_music() {
    if (music) {
        ma_sound_stop(music);
        ma_sound_uninit(music);
        delete music;
        music = nullptr;
    }
}

void AudioSystem::set_music_volume(float volume) {
    if (music) {
        ma_sound_set_volume(music, volume);
    }
}

void AudioSystem::set_master_volume(float volume) {
    if (engine) {
        ma_engine_set_volume(engine, volume);
    }
}

void AudioSystem::pause_all() {
    for (auto& [name, entry] : sounds) {
        if (entry.sound) {
            ma_sound_stop(entry.sound);
        }
    }
    if (music) {
        ma_sound_stop(music);
    }
}

void AudioSystem::resume_all() {
    for (auto& [name, entry] : sounds) {
        if (entry.sound) {
            ma_sound_start(entry.sound);
        }
    }
    if (music) {
        ma_sound_start(music);
    }
}
