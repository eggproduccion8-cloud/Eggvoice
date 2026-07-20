package de.maxhenkel.voicechat.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.gui.components.Button;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientPlayerStateManager;
import de.maxhenkel.voicechat.voice.common.ClientGroup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class VoiceChatScreenBase extends Screen {

    public static final int FONT_COLOR = 4210752;

    protected List<HoverArea> hoverAreas;
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    protected VoiceChatScreenBase(Component title, int xSize, int ySize) {
        super(title);
        this.xSize = xSize;
        this.ySize = ySize;
        this.hoverAreas = new ArrayList<>();
    }

    protected boolean hasSidebar() {
        return this instanceof VoiceChatScreen ||
               this instanceof de.maxhenkel.voicechat.gui.group.GroupScreen ||
               this instanceof de.maxhenkel.voicechat.gui.group.JoinGroupScreen ||
               this instanceof de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen ||
               this instanceof de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen;
    }

    @Override
    protected void init() {
        clearWidgets();
        super.init();

        if (hasSidebar()) {
            this.guiLeft = 131 + (width - 131 - this.xSize) / 2;
            this.guiTop = 33 + (height - 33 - this.ySize) / 2;
            addSidebar();
        } else {
            this.guiLeft = (width - this.xSize) / 2;
            this.guiTop = (height - this.ySize) / 2;
        }
    }

    protected void addSidebar() {
        int btnWidth = 100;
        int btnHeight = 20;
        int startX = 15;
        int startY = 45;

        // Button 1: CONTROLES
        Button btnControles = Button.builder(Component.literal("CONTROLES"), button -> {
            if (!(this instanceof VoiceChatScreen)) {
                minecraft.setScreen(new VoiceChatScreen());
            }
        }).bounds(startX, startY, btnWidth, btnHeight).build();
        if (this instanceof VoiceChatScreen) {
            btnControles.active = false;
        }
        addRenderableWidget(btnControles);

        // Button 2: GRUPOS
        Button btnGrupos = Button.builder(Component.literal("GRUPOS"), button -> {
            if (!(this instanceof de.maxhenkel.voicechat.gui.group.GroupScreen) && !(this instanceof de.maxhenkel.voicechat.gui.group.JoinGroupScreen)) {
                ClientPlayerStateManager stateManager = ClientManager.getPlayerStateManager();
                ClientGroup g = stateManager.getGroup();
                if (g != null) {
                    minecraft.setScreen(new de.maxhenkel.voicechat.gui.group.GroupScreen(g));
                } else {
                    minecraft.setScreen(new de.maxhenkel.voicechat.gui.group.JoinGroupScreen());
                }
            }
        }).bounds(startX, startY + 25, btnWidth, btnHeight).build();
        if (this instanceof de.maxhenkel.voicechat.gui.group.GroupScreen || this instanceof de.maxhenkel.voicechat.gui.group.JoinGroupScreen) {
            btnGrupos.active = false;
        }
        addRenderableWidget(btnGrupos);

        // Button 3: AJUSTES
        Button btnAjustes = Button.builder(Component.literal("AJUSTES"), button -> {
            if (!(this instanceof VoiceChatSettingsScreen)) {
                minecraft.setScreen(new VoiceChatSettingsScreen());
            }
        }).bounds(startX, startY + 50, btnWidth, btnHeight).build();
        if (this instanceof VoiceChatSettingsScreen) {
            btnAjustes.active = false;
        }
        addRenderableWidget(btnAjustes);

        // Button 4: VOLÚMENES
        Button btnVolumenes = Button.builder(Component.literal("VOLÚMENES"), button -> {
            if (!(this instanceof de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen)) {
                minecraft.setScreen(new de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen());
            }
        }).bounds(startX, startY + 75, btnWidth, btnHeight).build();
        if (this instanceof de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen) {
            btnVolumenes.active = false;
        }
        addRenderableWidget(btnVolumenes);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderForeground(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        // Draw modern strong dark-grey translucent background over the entire screen (AAA premium carbon slate theme)
        guiGraphics.fill(0, 0, width, height, 0xEE141519); // Deep, strong carbon slate gray

        if (hasSidebar()) {
            // Sidebar darker panels with glowing border
            guiGraphics.fill(0, 0, 130, height, 0x44000000); // Stronger black underlay for sidebar

            // Sidebar vertical separator
            guiGraphics.fill(130, 32, 131, height, 0x33FFFFFF); // White semi-transparent separator

            // Header separator across the screen
            guiGraphics.fill(0, 32, width, 33, 0x33FFFFFF); // White semi-transparent separator
        }

        // Draw top header text: VOICE WDP x EGG PRODUCTIONS ®
        String headerText = "VOICE WDP x EGG PRODUCTIONS ®";
        int headerWidth = minecraft.font.width(headerText);
        guiGraphics.drawString(minecraft.font, headerText, (width - headerWidth) / 2, 12, 0xFFFFFFFF, false);
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }

    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    protected boolean isIngame() {
        return minecraft.level != null;
    }

    protected int getFontColor() {
        return isIngame() ? FONT_COLOR : ChatFormatting.WHITE.getColor();
    }

    public void drawHoverAreas(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (HoverArea hoverArea : hoverAreas) {
            if (hoverArea.tooltip != null && hoverArea.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
                guiGraphics.renderTooltip(minecraft.font, hoverArea.tooltip.get(), mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    public static class HoverArea {
        private final int posX, posY;
        private final int width, height;
        @Nullable
        private final Supplier<List<FormattedCharSequence>> tooltip;

        public HoverArea(int posX, int posY, int width, int height) {
            this(posX, posY, width, height, null);
        }

        public HoverArea(int posX, int posY, int width, int height, Supplier<List<FormattedCharSequence>> tooltip) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.tooltip = tooltip;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Nullable
        public Supplier<List<FormattedCharSequence>> getTooltip() {
            return tooltip;
        }

        public boolean isHovered(int guiLeft, int guiTop, int mouseX, int mouseY) {
            if (mouseX >= guiLeft + posX && mouseX < guiLeft + posX + width) {
                if (mouseY >= guiTop + posY && mouseY < guiTop + posY + height) {
                    return true;
                }
            }
            return false;
        }
    }

}
