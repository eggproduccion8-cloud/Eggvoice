package de.maxhenkel.voicechat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private Minecraft minecraft;

    public static String currentlyPlayingSound = null;

    @Inject(method = "handleSystemChat", at = @At("HEAD"), cancellable = true)
    private void onHandleSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        String text = packet.content().getString();
        if (text.startsWith("[EGG_PLAY_SOUND]:")) {
            ci.cancel();
            String soundName = text.substring("[EGG_PLAY_SOUND]:".length()).trim();
            playEggSound(soundName);
        } else if (text.startsWith("[EGG_STOP_SOUND]")) {
            ci.cancel();
            currentlyPlayingSound = null;
            minecraft.execute(() -> {
                for (com.mojang.blaze3d.audio.Channel channel : de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels) {
                    channel.stop();
                }
                de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels.clear();
            });
        }
    }

    private void playEggSound(String soundName) {
        try {
            if (currentlyPlayingSound != null) {
                if (currentlyPlayingSound.equals(soundName)) {
                    return;
                }
                minecraft.execute(() -> {
                    for (com.mojang.blaze3d.audio.Channel channel : de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels) {
                        channel.stop();
                    }
                    de.maxhenkel.voicechat.gui.EggVoiceConfig.activeEggChannels.clear();
                });
            }

            currentlyPlayingSound = soundName;

            net.minecraft.resources.ResourceLocation soundLoc = new net.minecraft.resources.ResourceLocation("eggvoice", soundName);
            net.minecraft.sounds.SoundEvent soundEvent = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(soundLoc);
            minecraft.execute(() -> {
                minecraft.getSoundManager().play(
                    new de.maxhenkel.voicechat.gui.EggSoundInstance(soundEvent, 1.0F)
                );
            });
        } catch (Exception e) {
            // Ignore
        }
    }
}
