package de.maxhenkel.voicechat.permission;

import net.minecraft.server.level.ServerPlayer;
import de.maxhenkel.voicechat.Voicechat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.UUID;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ForgePermissionManager extends PermissionManager {

    @Override
    public Permission createPermissionInternal(String modId, String node, PermissionType type) {
        return new ForgePermission(new PermissionNode<>(modId, node, PermissionTypes.BOOLEAN, (player, playerUUID, context) -> type.hasPermission(player)), type);
    }

    @SubscribeEvent
    public void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(getPermissions().stream().map(ForgePermission.class::cast).map(ForgePermission::getNode).collect(Collectors.toList()));
    }

    // High-performance permission checker with cache for Mohist, Spigot, and LuckPerms using Java Reflection

    private static class CacheKey {
        final UUID playerUuid;
        final String node;

        CacheKey(UUID playerUuid, String node) {
            this.playerUuid = playerUuid;
            this.node = node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;
            CacheKey cacheKey = (CacheKey) o;
            return playerUuid.equals(cacheKey.playerUuid) && node.equals(cacheKey.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerUuid, node);
        }
    }

    private static class CacheValue {
        final boolean value;
        final long expiresAt;

        CacheValue(boolean value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private static final Map<CacheKey, CacheValue> PERMISSION_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public static boolean isLuckPermsPresent() {
        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            return providerClass.getMethod("get").invoke(null) != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isBukkitPresent() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean checkPermissionWithCache(ServerPlayer player, String nodeName, PermissionType defaultType) {
        long now = System.currentTimeMillis();
        CacheKey key = new CacheKey(player.getUUID(), nodeName);
        CacheValue cached = PERMISSION_CACHE.get(key);
        if (cached != null && now < cached.expiresAt) {
            return cached.value;
        }

        boolean result = checkPermission(player, nodeName, defaultType);

        PERMISSION_CACHE.put(key, new CacheValue(result, now + 1000L)); // Cache for 1 second
        return result;
    }

    private static boolean checkPermission(ServerPlayer player, String nodeName, PermissionType defaultType) {
        String system = "auto";
        try {
            if (Voicechat.SERVER_CONFIG != null && Voicechat.SERVER_CONFIG.permissionSystem != null) {
                system = Voicechat.SERVER_CONFIG.permissionSystem.get().toLowerCase(java.util.Locale.ROOT);
            }
        } catch (Throwable e) {
            // Ignore config errors
        }

        if ("auto".equals(system)) {
            if (isLuckPermsPresent()) {
                system = "luckperms";
            } else if (isBukkitPresent()) {
                system = "bukkit";
            } else {
                system = "forge";
            }
        }

        String eggNode = "eggvoice." + nodeName;
        String vcNode = "voicechat." + nodeName;

        if ("luckperms".equals(system)) {
            try {
                // Reflective LuckPerms check
                Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
                Object api = providerClass.getMethod("get").invoke(null);
                if (api != null) {
                    Object userManager = api.getClass().getMethod("getUserManager").invoke(api);
                    Object user = userManager.getClass().getMethod("getUser", UUID.class).invoke(userManager, player.getUUID());
                    if (user != null) {
                        Object cachedData = user.getClass().getMethod("getCachedData").invoke(user);
                        // In some LP versions, we might need queryOptions. Let's do it simply using calculatePermissionData
                        Object permissionData = cachedData.getClass().getMethod("getPermissionData").invoke(cachedData);

                        // Check eggNode
                        Object tristate1 = permissionData.getClass().getMethod("checkPermission", String.class).invoke(permissionData, eggNode);
                        if (tristate1 != null) {
                            String name = (String) tristate1.getClass().getMethod("name").invoke(tristate1);
                            if ("TRUE".equals(name)) return true;
                            if ("FALSE".equals(name)) return false;
                        }

                        // Check vcNode
                        Object tristate2 = permissionData.getClass().getMethod("checkPermission", String.class).invoke(permissionData, vcNode);
                        if (tristate2 != null) {
                            String name = (String) tristate2.getClass().getMethod("name").invoke(tristate2);
                            if ("TRUE".equals(name)) return true;
                            if ("FALSE".equals(name)) return false;
                        }
                    }
                }
            } catch (Throwable e) {
                // Fallback to Bukkit/Forge
            }
        }

        if ("bukkit".equals(system)) {
            try {
                Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                Object bukkitPlayer = bukkitClass.getMethod("getPlayer", UUID.class).invoke(null, player.getUUID());
                if (bukkitPlayer != null) {
                    // Check eggNode
                    boolean isSet1 = (boolean) bukkitPlayer.getClass().getMethod("isPermissionSet", String.class).invoke(bukkitPlayer, eggNode);
                    if (isSet1) {
                        return (boolean) bukkitPlayer.getClass().getMethod("hasPermission", String.class).invoke(bukkitPlayer, eggNode);
                    }
                    // Check vcNode
                    boolean isSet2 = (boolean) bukkitPlayer.getClass().getMethod("isPermissionSet", String.class).invoke(bukkitPlayer, vcNode);
                    if (isSet2) {
                        return (boolean) bukkitPlayer.getClass().getMethod("hasPermission", String.class).invoke(bukkitPlayer, vcNode);
                    }
                    // Fallback to hasPermission
                    return (boolean) bukkitPlayer.getClass().getMethod("hasPermission", String.class).invoke(bukkitPlayer, eggNode) ||
                           (boolean) bukkitPlayer.getClass().getMethod("hasPermission", String.class).invoke(bukkitPlayer, vcNode);
                }
            } catch (Throwable e) {
                // Fallback
            }
        }

        // Forge permission check / default fallback
        return defaultType.hasPermission(player);
    }
}
