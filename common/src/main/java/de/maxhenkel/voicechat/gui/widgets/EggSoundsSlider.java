package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.voicechat.gui.EggVoiceConfig;
import net.minecraft.network.chat.Component;

public class EggSoundsSlider extends DebouncedSlider {

    public EggSoundsSlider(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty(), EggVoiceConfig.eggSoundsVolume);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(getMsg());
    }

    public Component getMsg() {
        return Component.literal("Sonidos Egg: " + Math.round(value * 100F) + "%");
    }

    @Override
    protected void applyValue() {
        EggVoiceConfig.setVolume((float) value);
    }

    @Override
    public void applyDebounced() {
        EggVoiceConfig.setVolume((float) value);
        EggVoiceConfig.save();
    }
}
