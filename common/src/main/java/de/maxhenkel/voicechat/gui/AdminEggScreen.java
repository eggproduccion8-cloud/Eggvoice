package de.maxhenkel.voicechat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import de.maxhenkel.voicechat.command.VoicechatCommands;
import de.maxhenkel.voicechat.gui.widgets.TransparentButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminEggScreen extends VoiceChatScreenBase {

    private static final Component TITLE = Component.literal("Apartado de administración Egg");

    private final List<String> sounds = new ArrayList<>();
    private final List<PlayerInfo> players = new ArrayList<>();
    private final Set<String> selectedPlayers = new HashSet<>();
    private String selectedSound = "song1";

    private Button stopButton;
    private Button playButton;
    private Button megaphoneButton;
    private Button createChannelButton;
    private Button closeChannelButton;
    private Button backButton;

    public static boolean megaphoneActive = false;
    public static boolean channelActive = false;

    public static String lastPlayedSound = "Ninguno";
    public static String lastPlayedTargets = "Nadie";

    private int soundOffset = 0;
    private int playerOffset = 0;

    public AdminEggScreen() {
        super(TITLE, 250, 200);
        for (int i = 1; i <= 40; i++) {
            sounds.add("song" + i);
        }
    }

    @Override
    protected void init() {
        super.init();

        players.clear();
        if (minecraft.getConnection() != null) {
            players.addAll(minecraft.getConnection().getOnlinePlayers());
        }

        int mainAreaX = 131;
        int mainAreaWidth = width - 131;
        int btnW = 110;
        int btnH = 20;

        // Button to Stop Sound
        stopButton = new TransparentButton(mainAreaX + 10, height - 105, 230, btnH, Component.literal("DETENER SONIDOS"), button -> {
            if (!selectedPlayers.isEmpty() && minecraft.player != null) {
                String targets = String.join(" ", selectedPlayers);
                minecraft.player.connection.sendCommand(VoicechatCommands.VOICECHAT_COMMAND + " stop " + targets);
                lastPlayedSound = "Ninguno";
                lastPlayedTargets = "Nadie";
            }
        });
        addRenderableWidget(stopButton);

        // Button to Play Sound
        playButton = new TransparentButton(mainAreaX + 10, height - 80, btnW, btnH, Component.literal("REPRODUCIR"), button -> {
            if (selectedSound != null && !selectedPlayers.isEmpty()) {
                String targets = String.join(" ", selectedPlayers);
                if (minecraft.player != null) {
                    minecraft.player.connection.sendCommand(VoicechatCommands.VOICECHAT_COMMAND + " play " + selectedSound + " " + targets);
                    lastPlayedSound = selectedSound;
                    lastPlayedTargets = String.join(", ", selectedPlayers);
                }
            }
        });
        addRenderableWidget(playButton);

        // Megaphone Toggle Button
        String megLabel = megaphoneActive ? "MEGÁFONO: SI" : "MEGÁFONO: NO";
        megaphoneButton = new TransparentButton(mainAreaX + 130, height - 80, btnW, btnH, Component.literal(megLabel), button -> {
            if (minecraft.player != null) {
                minecraft.player.connection.sendCommand(VoicechatCommands.VOICECHAT_COMMAND + " megaphone");
                megaphoneActive = !megaphoneActive;
                button.setMessage(Component.literal(megaphoneActive ? "MEGÁFONO: SI" : "MEGÁFONO: NO"));
            }
        });
        addRenderableWidget(megaphoneButton);

        // Create Private Channel Button
        createChannelButton = new TransparentButton(mainAreaX + 10, height - 55, btnW, btnH, Component.literal("INICIAR CANAL"), button -> {
            if (!selectedPlayers.isEmpty() && minecraft.player != null) {
                String targets = String.join(" ", selectedPlayers);
                minecraft.player.connection.sendCommand(VoicechatCommands.VOICECHAT_COMMAND + " channel create " + targets);
                channelActive = true;
                updateButtons();
            }
        });
        addRenderableWidget(createChannelButton);

        // Close Private Channel Button
        closeChannelButton = new TransparentButton(mainAreaX + 130, height - 55, btnW, btnH, Component.literal("CERRAR CANAL"), button -> {
            if (minecraft.player != null) {
                minecraft.player.connection.sendCommand(VoicechatCommands.VOICECHAT_COMMAND + " channel close");
                channelActive = false;
                updateButtons();
            }
        });
        addRenderableWidget(closeChannelButton);

        // Back Button
        backButton = new TransparentButton(mainAreaX + (mainAreaWidth - 180) / 2, height - 30, 180, btnH, Component.literal("VOLVER"), button -> {
            minecraft.setScreen(new VoiceChatScreen());
        });
        addRenderableWidget(backButton);

        updateButtons();
    }

    private void updateButtons() {
        if (playButton != null) {
            playButton.active = selectedSound != null && !selectedPlayers.isEmpty();
        }
        if (stopButton != null) {
            stopButton.active = !selectedPlayers.isEmpty();
        }
        if (createChannelButton != null) {
            createChannelButton.active = !selectedPlayers.isEmpty();
        }
        if (closeChannelButton != null) {
            closeChannelButton.active = true; // Always active for operators so they can always close any active channels!
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mainAreaX = 131;
        int mainAreaWidth = width - 131;
        int contentY = 45;

        if (mouseX >= mainAreaX + 10 && mouseX <= mainAreaX + 110) {
            int clickedIdx = (int) ((mouseY - contentY) / 12) + soundOffset;
            if (clickedIdx >= 0 && clickedIdx < sounds.size() && mouseY >= contentY && mouseY <= contentY + 110) {
                selectedSound = sounds.get(clickedIdx);
                updateButtons();
                return true;
            }
        }

        if (mouseX >= mainAreaX + 120 && mouseX <= mainAreaX + mainAreaWidth - 10) {
            int clickedIdx = (int) ((mouseY - contentY) / 12) + playerOffset;
            if (clickedIdx >= 0 && clickedIdx < players.size() && mouseY >= contentY && mouseY <= contentY + 110) {
                String pName = players.get(clickedIdx).getProfile().getName();
                if (selectedPlayers.contains(pName)) {
                    selectedPlayers.remove(pName);
                } else {
                    selectedPlayers.add(pName);
                }
                updateButtons();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int mainAreaX = 131;

        if (mouseX >= mainAreaX + 10 && mouseX <= mainAreaX + 110) {
            soundOffset = Math.max(0, Math.min(sounds.size() - 8, soundOffset - (int) delta));
            return true;
        }

        if (mouseX >= mainAreaX + 120 && mouseX <= mainAreaX + 120 + 110) {
            playerOffset = Math.max(0, Math.min(players.size() - 8, playerOffset - (int) delta));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int mainAreaX = 131;
        int contentY = 45;

        guiGraphics.drawString(font, "SONIDOS (Scroll)", mainAreaX + 10, contentY - 12, 0xFF55FF55, false);
        guiGraphics.drawString(font, "JUGADORES (X)", mainAreaX + 120, contentY - 12, 0xFFFF5555, false);

        for (int i = 0; i < 8; i++) {
            int idx = i + soundOffset;
            if (idx < sounds.size()) {
                String s = sounds.get(idx);
                int color = s.equals(selectedSound) ? 0xFFFFFF55 : 0xFFFFFFFF;
                String prefix = s.equals(selectedSound) ? "> " : "  ";
                guiGraphics.drawString(font, prefix + s, mainAreaX + 10, contentY + (i * 12), color, false);
            }
        }

        for (int i = 0; i < 8; i++) {
            int idx = i + playerOffset;
            if (idx < players.size()) {
                PlayerInfo p = players.get(idx);
                String name = p.getProfile().getName();
                boolean selected = selectedPlayers.contains(name);
                int color = selected ? 0xFF55FF55 : 0xFFFFFFFF;
                String prefix = selected ? "-> " : "   ";
                guiGraphics.drawString(font, prefix + name, mainAreaX + 120, contentY + (i * 12), color, false);
            }
        }

        // Show currently/last playing status beautifully!
        String targetsDisp = lastPlayedTargets;
        if (targetsDisp.length() > 25) {
            targetsDisp = targetsDisp.substring(0, 22) + "...";
        }
        guiGraphics.drawString(font, "Sonando: " + lastPlayedSound, mainAreaX + 10, height - 128, 0xFFFFFF55, false);
        guiGraphics.drawString(font, "Para: " + targetsDisp, mainAreaX + 10, height - 118, 0xFF888888, false);
    }
}
