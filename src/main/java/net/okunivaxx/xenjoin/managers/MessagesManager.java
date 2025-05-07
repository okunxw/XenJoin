package net.okunivaxx.xenjoin.managers;

import net.okunivaxx.xenjoin.XenJoin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesManager {

    private final File file;
    private FileConfiguration config;
    private final XenJoin plugin;

    public MessagesManager(XenJoin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        reloadMessages();
    }

    public String getMessage(String path) {
        String finalPath = "messages." + path;

        if (!config.contains(finalPath)) {
            plugin.getLogger().warning("⚠ Внимание: Ключ '" + path + "' отсутствует в messages.yml!");
        }

        return formatMessage(config.getString(finalPath, "§cXenJoin §8» §cОшибка: Ключ '" + path + "' отсутствует в messages.yml!"));
    }

    public List<String> getMessageList(String path) {
        String finalPath = config.contains("messages." + path) ? "messages." + path : path;
        return config.getStringList(finalPath).stream()
                .map(this::formatMessage)
                .collect(Collectors.toList());
    }

    public void reloadMessages() {
        if (file.exists()) {
            this.config = YamlConfiguration.loadConfiguration(file);
        }
    }

    public String formatMessage(String text) {
        if (text == null) return "§cОшибка: Сообщение не найдено!";

        // 🔥 Корректная обработка HEX-кодов
        text = ChatColor.translateAlternateColorCodes('&', text);
        while (text.contains("&x")) {
            text = text.replaceFirst("&x([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                    ChatColor.of("#$1$2$3$4$5$6").toString());
        }
        return text;
    }
}