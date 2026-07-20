package de.maxhenkel.voicechat.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TransparentButton extends Button {

    public TransparentButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            int bgColor = this.active ? (this.isHovered() ? 0x55FFFFFF : 0x22FFFFFF) : 0x11FFFFFF;
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);

            int borderColor = this.active ? (this.isHovered() ? 0x88FFFFFF : 0x44FFFFFF) : 0x22FFFFFF;
            guiGraphics.fill(getX() - 1, getY() - 1, getX() + getWidth() + 1, getY(), borderColor);
            guiGraphics.fill(getX() - 1, getY(), getX(), getY() + getHeight(), borderColor);
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth() + 1, getY() + getHeight(), borderColor);
            guiGraphics.fill(getX() - 1, getY() + getHeight(), getX() + getWidth() + 1, getY() + getHeight() + 1, borderColor);

            int color = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
            Component boldMessage = getMessage().copy().withStyle(net.minecraft.ChatFormatting.BOLD);
            int textWidth = Minecraft.getInstance().font.width(boldMessage);
            guiGraphics.drawString(Minecraft.getInstance().font, boldMessage, getX() + (getWidth() - textWidth) / 2, getY() + (getHeight() - 8) / 2, color, false);
        }
    }
}
