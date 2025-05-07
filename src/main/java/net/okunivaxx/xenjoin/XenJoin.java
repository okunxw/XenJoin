package net.okunivaxx.xenjoin;

import net.luckperms.api.LuckPerms;
import net.okunivaxx.xenjoin.commands.CommandHandler;
import net.okunivaxx.xenjoin.config.ConfigManager;
import net.okunivaxx.xenjoin.managers.MessagesManager;
import net.okunivaxx.xenjoin.managers.MenuManager;
import net.okunivaxx.xenjoin.managers.PermissionsManager;
import net.okunivaxx.xenjoin.listeners.JoinListener;
import net.okunivaxx.xenjoin.listeners.MenuListener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class XenJoin extends JavaPlugin {

    private ConfigManager configManager;
    private PermissionsManager permissionsManager;
    private MessagesManager messagesManager;
    private MenuManager menuManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        permissionsManager = new PermissionsManager(this);
        menuManager = new MenuManager(messagesManager);

        saveDefaultConfig();
        setupLuckPerms();
        registerListeners();
        registerCommands();

        getLogger().info("✅ XenJoin включен!");
    }

    private void setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            getLogger().info("✅ LuckPerms успешно интегрирован!");
        } else {
            getLogger().warning("⚠ LuckPerms не найден, используем стандартные права.");
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(configManager, permissionsManager, luckPerms), this);
        getServer().getPluginManager().registerEvents(new MenuListener(configManager, messagesManager), this);
    }

    private void registerCommands() {
        getCommand("xenjoin").setExecutor(new CommandHandler(configManager, messagesManager, menuManager, permissionsManager));
        getCommand("xenjoin").setTabCompleter(new XenJoinTabCompleter());
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("❌ XenJoin выключен!");
    }
}