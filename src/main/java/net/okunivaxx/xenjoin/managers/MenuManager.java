package net.okunivaxx.xenjoin.managers;

import net.okunivaxx.xenjoin.XenJoin;
import net.okunivaxx.xenjoin.managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class MenuManager {

    private final MessagesManager messagesManager;

    public MenuManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public Inventory createMainMenu() {
        // Загружаем заголовок из messages.yml
        String menuTitle = ChatColor.translateAlternateColorCodes('&', messagesManager.getMessage("menu-title"));

        // Создаем меню
        Inventory menu = Bukkit.createInventory(null, 27, menuTitle);

        // Добавляем элементы в меню
        menu.setItem(11, createMenuItem(Material.REDSTONE, "menu-reload", "menu-reload-lore"));
        menu.setItem(13, createMenuItem(Material.NAME_TAG, "menu-setmessage", "menu-setmessage-lore"));
        menu.setItem(15, createMenuItem(Material.BARRIER, "menu-reset", "menu-reset-lore"));
        menu.setItem(26, createMenuItem(Material.BARRIER, "menu-close", "menu-close-lore"));

        return menu;
    }

    private ItemStack createMenuItem(Material material, String nameKey, String loreKey) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Загружаем название и lore из messages.yml
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', messagesManager.getMessage(nameKey)));
            meta.setLore(messagesManager.getMessageList(loreKey));

            item.setItemMeta(meta);
        }
        return item;
    }
}
