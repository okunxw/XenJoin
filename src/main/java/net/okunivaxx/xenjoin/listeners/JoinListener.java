package net.okunivaxx.xenjoin.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.okunivaxx.xenjoin.config.ConfigManager;
import net.okunivaxx.xenjoin.managers.PermissionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class JoinListener implements Listener {
    private final ConfigManager configManager;
    private final PermissionsManager permissionsManager;
    private final LuckPerms luckPerms;

    public JoinListener(ConfigManager configManager, PermissionsManager permissionsManager, LuckPerms luckPerms) {
        this.configManager = configManager;
        this.permissionsManager = permissionsManager;
        this.luckPerms = luckPerms;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Получаем префикс и суффикс через LuckPerms
        String prefix = getLuckPermsPrefix(player);
        String suffix = getLuckPermsSuffix(player);

        // Загружаем стандартное приветствие
        String finalMessage = configManager.getConfig().getString("join-message",
                        "§c§lWELCOME §8 ⇌ %prefix% §7%player% %suffix% §aприсоединился к серверу!")
                .replace("%player%", player.getName())
                .replace("%prefix%", prefix)
                .replace("%suffix%", suffix)
                .replace("&", "§");

        AtomicReference<String> finalMessageRef = new AtomicReference<>(finalMessage);

        // Проверяем кастомное приветствие
        Optional.ofNullable(configManager.getConfig().getConfigurationSection("greeting-messages"))
                .ifPresent(section -> {
                    for (String group : section.getKeys(false)) {
                        String permissionNode = "xenjoin.greet." + group;
                        if (permissionsManager.hasPermission(player, permissionNode)) {
                            finalMessageRef.set(section.getString(group, finalMessageRef.get())
                                    .replace("%player%", player.getName())
                                    .replace("%prefix%", prefix)
                                    .replace("%suffix%", suffix)
                                    .replace("&", "§"));
                            break;
                        }
                    }
                });

        // Устанавливаем приветствие
        event.setJoinMessage(finalMessageRef.get());
    }

    private String getLuckPermsPrefix(Player player) {
        if (luckPerms == null) return "";
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId()))
                .map(user -> user.getCachedData().getMetaData(luckPerms.getContextManager().getQueryOptions(player)).getPrefix())
                .orElse("");
    }

    private String getLuckPermsSuffix(Player player) {
        if (luckPerms == null) return "";
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId()))
                .map(user -> user.getCachedData().getMetaData(luckPerms.getContextManager().getQueryOptions(player)).getSuffix())
                .orElse("");
    }
}