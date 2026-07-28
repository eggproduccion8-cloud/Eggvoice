package de.maxhenkel.voicechat.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.ChannelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Inject(method = "play", at = @At("TAIL"))
    private void onPlay(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance instanceof de.maxhenkel.voicechat.gui.EggSoundInstance) {
            ChannelAccess.ChannelHandle handle = instanceToChannel.get(soundInstance);
            if (handle != null) {
                com.mojang.blaze3d.audio.Channel channel = ((ChannelHandleAccessor) (Object) handle).getChannel();
                if (channel != null) {
                    de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels.removeIf(com.mojang.blaze3d.audio.Channel::stopped);
                    de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels.add(channel);
                    channel.setVolume(de.maxhenkel.voicechat.gui.EggVoiceConfig.eggSoundsVolume);
                }
            }
        }
    }
}
