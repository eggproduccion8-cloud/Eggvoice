package de.maxhenkel.voicechat.gui;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class EggSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {

    private final float baseVolume;
    private boolean stopped = false;

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

    @Override
    public boolean isStopped() {
        return this.stopped;
    }

    @Override
    public void tick() {
        // Keeps the sound playing and allows the sound engine to update the volume in real-time
    }

    public void stopSound() {
        this.stopped = true;
    }
}
