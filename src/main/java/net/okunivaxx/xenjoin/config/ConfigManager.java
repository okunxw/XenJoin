package net.okunivaxx.xenjoin.config;

import net.okunivaxx.xenjoin.XenJoin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ConfigManager {

    private final XenJoin plugin;
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(XenJoin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        reloadConfig();
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения config.yml!");
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getJoinMessage() {
        return Optional.ofNullable(config.getString("join-message"))
                .orElse("§cПриветствие не установлено!");
    }

    public void setJoinMessage(String message) {
        config.set("join-message", message);
        saveConfig();
    }

    public void resetJoinMessage() {
        config.set("join-message", "§c§lWELCOME §8⇌ %prefix% §7%player% %suffix% §aприсоединился к серверу!");
        config.set("approved-greeting", null);
        config.set("approved-timestamp", null);
        saveConfig();
    }

    public List<String> getSuggestedGreetings() {
        return config.getStringList("suggested-greetings");
    }

    public void addSuggestedGreeting(String suggestion) {
        List<String> greetings = config.getStringList("suggested-greetings");
        greetings.add(suggestion);
        config.set("suggested-greetings", greetings);
        saveConfig();
    }

    public void voteForGreeting(int voteId, Player player) {
        List<String> greetings = config.getStringList("suggested-greetings");

        if (voteId < 0 || voteId >= greetings.size()) {
            player.sendMessage("§cОшибка: ID приветствия недействителен.");
            return;
        }

        String playerKey = "votes." + player.getUniqueId().toString();
        if (config.isSet(playerKey)) {
            player.sendMessage("§cВы уже голосовали!");
            return;
        }

        int votes = config.getInt("votes." + voteId, 0);
        config.set("votes." + voteId, votes + 1);
        config.set(playerKey, voteId);
        saveConfig();

        player.sendMessage("§aВы успешно проголосовали!");
    }

    public int getVotesForGreeting(int voteId) {
        return config.getInt("votes." + voteId, 0);
    }

    public XenJoin getPlugin() {
        return plugin;
    }
}