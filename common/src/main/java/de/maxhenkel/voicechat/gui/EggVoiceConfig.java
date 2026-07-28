package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.Voicechat;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EggVoiceConfig {

    public static float eggSoundsVolume = 1.0f;

    public static final java.util.List<java.util.function.Consumer<Float>> volumeListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    public static final java.util.List<com.mojang.blaze3d.audio.Channel> activeEggChannels = new java.util.concurrent.CopyOnWriteArrayList<>();

    private static final Path CONFIG_FILE = Voicechat.getModConfigFolder().resolve("egg_voice_sounds.properties");

    static {
        load();
        volumeListeners.add(volume -> {
            activeEggChannels.removeIf(com.mojang.blaze3d.audio.Channel::stopped);
            for (com.mojang.blaze3d.audio.Channel channel : activeEggChannels) {
                channel.setVolume(volume);
            }
        });
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
                    props.load(in);
                }
                eggSoundsVolume = Float.parseFloat(props.getProperty("eggSoundsVolume", "1.0"));
            }
        } catch (Exception e) {
            // Keep default
        }
    }

    public static void setVolume(float volume) {
        eggSoundsVolume = volume;
        for (java.util.function.Consumer<Float> listener : volumeListeners) {
            try {
                listener.accept(volume);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            Properties props = new Properties();
            props.setProperty("eggSoundsVolume", String.valueOf(eggSoundsVolume));
            try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
                props.store(out, "Egg Voice Sounds Config");
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
