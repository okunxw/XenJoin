package net.okunivaxx.xenjoin.listeners;

import net.okunivaxx.xenjoin.config.ConfigManager;
import net.okunivaxx.xenjoin.managers.MessagesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;

    public MenuListener(ConfigManager configManager, MessagesManager messagesManager) {
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        // Проверяем название меню через messages.yml
        if (event.getView().getTitle().equals(messagesManager.getMessage("menu-title"))) {
            event.setCancelled(true); // Блокируем перемещение предметов

            switch (event.getRawSlot()) {
                case 11: // Слот для "Перезагрузить конфиг"
                    configManager.reloadConfig();
                    player.sendMessage(messagesManager.getMessage("config-reloaded"));
                    break;

                case 13: // Слот для "Установить сообщение"
                    player.sendMessage(messagesManager.getMessage("setmessage-usage"));
                    break;

                case 15: // Слот для "Сбросить приветствия"
                    configManager.resetJoinMessage();
                    player.sendMessage(messagesManager.getMessage("reset-success"));
                    player.closeInventory();
                    break;

                case 26: // Слот для "Закрыть меню"
                    player.closeInventory();
                    break;
            }
        }
    }
}
