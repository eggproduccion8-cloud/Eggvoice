package de.maxhenkel.voicechat.mixin;

import net.minecraft.client.sounds.ChannelAccess;
import com.mojang.blaze3d.audio.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChannelAccess.ChannelHandle.class)
public interface ChannelHandleAccessor {
    @Accessor("channel")
    Channel getChannel();
}
