package de.maxhenkel.voicechat.plugins.impl.events;

import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.events.OpenALSoundEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class OpenALSoundEventImpl extends ClientEventImpl implements OpenALSoundEvent.Pre, OpenALSoundEvent.Post {

    @Nullable
    private final UUID channelId;
    @Nullable
    private final Position position;
    @Nullable
    private final String category;
    private final int source;

    public OpenALSoundEventImpl(@Nullable UUID channelId, @Nullable Position position, @Nullable String category, int source) {
        this.channelId = channelId;
        this.position = position;
        this.category = category;
        this.source = source;
    }

    @Nullable
    @Override
    public Position getPosition() {
        return position;
    }

    @Nullable
    @Override
    public UUID getChannelId() {
        return channelId;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Nullable
    @Override
    public String getCategory() {
        return category;
    }
}
