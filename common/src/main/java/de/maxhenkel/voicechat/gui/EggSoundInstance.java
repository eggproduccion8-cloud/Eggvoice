package de.maxhenkel.voicechat.gui;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class EggSoundInstance extends SimpleSoundInstance {

    private final float baseVolume;

    public EggSoundInstance(SoundEvent sound, float volume) {
        super(
            sound.getLocation(),
            SoundSource.RECORDS,
            volume,
            1.0F,
            net.minecraft.util.RandomSource.create(),
            true,
            0,
            net.minecraft.client.resources.sounds.SoundInstance.Attenuation.NONE,
            0.0D,
            0.0D,
            0.0D,
            true
        );
        this.baseVolume = volume;
    }

    @Override
    public float getVolume() {
        return this.baseVolume * de.maxhenkel.voicechat.gui.EggVoiceConfig.eggSoundsVolume;
    }
}
