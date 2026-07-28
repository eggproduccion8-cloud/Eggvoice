package de.maxhenkel.voicechat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.group.GroupScreen;
import de.maxhenkel.voicechat.gui.group.JoinGroupScreen;
import de.maxhenkel.voicechat.gui.tooltips.DisableTooltipSupplier;
import de.maxhenkel.voicechat.gui.tooltips.HideTooltipSupplier;
import de.maxhenkel.voicechat.gui.tooltips.MuteTooltipSupplier;
import de.maxhenkel.voicechat.gui.tooltips.RecordingTooltipSupplier;
import de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen;
import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import de.maxhenkel.voicechat.gui.widgets.ToggleImageButton;
import de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager;
import de.maxhenkel.voicechat.voice.client.*;
import de.maxhenkel.voicechat.voice.common.ClientGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class VoiceChatScreen extends VoiceChatScreenBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_voicechat.png");
    private static final ResourceLocation MICROPHONE = new ResourceLocation(Voicechat.MODID, "textures/icons/microphone_button.png");
    private static final ResourceLocation HIDE = new ResourceLocation(Voicechat.MODID, "textures/icons/hide_button.png");
    private static final ResourceLocation VOLUMES = new ResourceLocation(Voicechat.MODID, "textures/icons/adjust_volumes.png");
    private static final ResourceLocation SPEAKER = new ResourceLocation(Voicechat.MODID, "textures/icons/speaker_button.png");
    private static final ResourceLocation RECORD = new ResourceLocation(Voicechat.MODID, "textures/icons/record_button.png");
    private static final Component TITLE = Component.translatable("gui.voicechat.voice_chat.title");
    private static final Component SETTINGS = Component.translatable("message.voicechat.settings");
    private static final Component GROUP = Component.translatable("message.voicechat.group");
    public static final Component ADJUST_PLAYER_VOLUMES = Component.translatable("message.voicechat.adjust_volumes");

    private Button btnMute;
    private Button btnDisable;
    private Button btnHide;
    @Nullable
    private Button btnRecord;
    private HoverArea recordingHoverArea;

    private ClientPlayerStateManager stateManager;

    public VoiceChatScreen() {
        super(TITLE, 195, 76);
        stateManager = ClientManager.getPlayerStateManager();
    }

    @Override
    protected void init() {
        super.init();
        @Nullable ClientVoicechat client = ClientManager.getClient();

        int mainAreaX = 131;
        int mainAreaWidth = width - 131;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int startX = mainAreaX + (mainAreaWidth - buttonWidth) / 2;
        int startY = 60;

        btnMute = new de.maxhenkel.voicechat.gui.widgets.TransparentButton(startX, startY, buttonWidth, buttonHeight, Component.empty(), button -> {
            stateManager.setMuted(!stateManager.isMuted());
            updateButtonTexts();
        });
        addRenderableWidget(btnMute);

        btnDisable = new de.maxhenkel.voicechat.gui.widgets.TransparentButton(startX, startY + 25, buttonWidth, buttonHeight, Component.empty(), button -> {
            stateManager.setDisabled(!stateManager.isDisabled());
            updateButtonTexts();
        });
        addRenderableWidget(btnDisable);

        btnHide = new de.maxhenkel.voicechat.gui.widgets.TransparentButton(startX, startY + 50, buttonWidth, buttonHeight, Component.empty(), button -> {
            boolean newVal = !VoicechatClient.CLIENT_CONFIG.hideIcons.get();
            VoicechatClient.CLIENT_CONFIG.hideIcons.set(newVal).save();
            updateButtonTexts();
        });
        addRenderableWidget(btnHide);

        if (client != null && VoicechatClient.CLIENT_CONFIG.useNatives.get()) {
            if (client.getRecorder() != null || (client.getConnection() != null && client.getConnection().getData().allowRecording())) {
                btnRecord = new de.maxhenkel.voicechat.gui.widgets.TransparentButton(startX, startY + 75, buttonWidth, buttonHeight, Component.empty(), button -> {
                    toggleRecording();
                    updateButtonTexts();
                });
                addRenderableWidget(btnRecord);
            }
        }

        if (minecraft.player != null && minecraft.player.hasPermissions(2)) {
            Button btnAdmin = new de.maxhenkel.voicechat.gui.widgets.TransparentButton(startX, startY + 100, buttonWidth, buttonHeight, Component.literal("Panel Admin Egg"), button -> {
                minecraft.setScreen(new AdminEggScreen());
            });
            addRenderableWidget(btnAdmin);
        }

        int relStartX = startX - guiLeft;
        int relStartY = startY - guiTop;
        recordingHoverArea = new HoverArea(relStartX, relStartY + 125, buttonWidth, 20);

        updateButtonTexts();
        checkButtons();
    }

    private void updateButtonTexts() {
        if (btnMute != null) {
            String state = stateManager.isMuted() ? "SILENCIADO" : "ACTIVO";
            btnMute.setMessage(Component.literal("Micrófono: " + state));
        }
        if (btnDisable != null) {
            String state = stateManager.isDisabled() ? "DESACTIVADO" : "ACTIVO";
            btnDisable.setMessage(Component.literal("Sonido: " + state));
        }
        if (btnHide != null) {
            String state = VoicechatClient.CLIENT_CONFIG.hideIcons.get() ? "OCULTOS" : "VISIBLES";
            btnHide.setMessage(Component.literal("Iconos HUD: " + state));
        }
        if (btnRecord != null) {
            boolean recording = ClientManager.getClient() != null && ClientManager.getClient().getRecorder() != null;
            String state = recording ? "DETENER" : "INICIAR";
            btnRecord.setMessage(Component.literal("Grabación de Audio: " + state));
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonTexts();
        checkButtons();
    }

    private void checkButtons() {
        if (btnMute != null) {
            btnMute.active = MuteTooltipSupplier.canMuteMic();
        }
        if (btnDisable != null) {
            btnDisable.active = stateManager.canEnable();
        }
    }

    private void toggleRecording() {
        ClientVoicechat c = ClientManager.getClient();
        if (c == null) {
            return;
        }
        c.toggleRecording();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ClientCompatibilityManager.INSTANCE.getBoundKeyOf(KeyEvents.KEY_VOICE_CHAT).getValue()) {
            minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // Draw a sleek, darker professional card panel in the center of the right main content area
        int mainAreaX = 131;
        int mainAreaWidth = width - 131;
        int cardWidth = 230;
        int cardHeight = height - 90;
        int cardX = mainAreaX + (mainAreaWidth - cardWidth) / 2;
        int cardY = 50;

        // Draw shadow/darker card background
        guiGraphics.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, 0x44000000);

        // Draw a nice subtle glowing border around the card to make it look clean and professional
        guiGraphics.fill(cardX - 1, cardY - 1, cardX + cardWidth + 1, cardY, 0x22FFFFFF);
        guiGraphics.fill(cardX - 1, cardY, cardX, cardY + cardHeight, 0x22FFFFFF);
        guiGraphics.fill(cardX + cardWidth, cardY, cardX + cardWidth + 1, cardY + cardHeight, 0x22FFFFFF);
        guiGraphics.fill(cardX - 1, cardY + cardHeight, cardX + cardWidth + 1, cardY + cardHeight + 1, 0x22FFFFFF);
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        ClientVoicechat client = ClientManager.getClient();
        if (client != null && client.getRecorder() != null) {
            AudioRecorder recorder = client.getRecorder();
            MutableComponent time = Component.literal(recorder.getDuration());
            guiGraphics.drawString(font, time.withStyle(ChatFormatting.DARK_RED), guiLeft + recordingHoverArea.getPosX() + recordingHoverArea.getWidth() / 2 - font.width(time) / 2, guiTop + recordingHoverArea.getPosY() + recordingHoverArea.getHeight() / 2 - font.lineHeight / 2, 0, false);

            if (recordingHoverArea.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, Component.translatable("message.voicechat.storage_size", recorder.getStorage()), mouseX, mouseY);
            }
        }
    }

}
