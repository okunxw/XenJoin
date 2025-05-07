package net.okunivaxx.xenjoin.managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.Plugin;

public class PermissionsManager {

    private LuckPerms luckPerms;

    public PermissionsManager(Plugin plugin) {
        RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            plugin.getLogger().info("✅ LuckPerms подключен!");
        } else {
            plugin.getLogger().warning("⚠ LuckPerms не найден, используем стандартные права.");
        }
    }

    public boolean hasPermission(Player player, String permission) {
        if (luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                return user.getCachedData().getPermissionData(QueryOptions.defaultContextualOptions()).checkPermission(permission).asBoolean();
            }
        }
        return player.hasPermission(permission);
    }
}
