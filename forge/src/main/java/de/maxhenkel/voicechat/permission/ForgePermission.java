package de.maxhenkel.voicechat.permission;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionNode;

public class ForgePermission implements Permission {

    private final PermissionNode<Boolean> node;
    private final PermissionType type;

    public ForgePermission(PermissionNode<Boolean> node, PermissionType type) {
        this.node = node;
        this.type = type;
    }

    @Override
    public boolean hasPermission(ServerPlayer player) {
        String nodeName = node.getNodeName();
        if ("admin".equals(nodeName)) {
            // Admin permission strictly requires being OP (level 2 or higher) and does NOT depend on LuckPerms/external APIs!
            return player != null && player.hasPermissions(2);
        }
        // Speak, listen, and groups always return true (everyone is allowed without permissions!)
        return true;
    }

    @Override
    public PermissionType getPermissionType() {
        return type;
    }

    public PermissionNode<Boolean> getNode() {
        return node;
    }
}
