package net.okunivaxx.xenjoin.commands;

import net.okunivaxx.xenjoin.config.ConfigManager;
import net.okunivaxx.xenjoin.managers.MessagesManager;
import net.okunivaxx.xenjoin.managers.MenuManager;
import net.okunivaxx.xenjoin.managers.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final MenuManager menuManager;
    private final PermissionsManager permissionsManager;

    public CommandHandler(ConfigManager configManager, MessagesManager messagesManager, MenuManager menuManager, PermissionsManager permissionsManager) {
        this.configManager = configManager;
        this.messagesManager = messagesManager;
        this.menuManager = menuManager;
        this.permissionsManager = permissionsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("xenjoin")) return false;

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!(sender instanceof Player player)) {
                    List<String> playerOnlyMessage = messagesManager.getMessageList("player-only");
                    playerOnlyMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
                    return false;
                }

                if (!permissionsManager.hasPermission(player, "xenjoin.reload")) {
                    List<String> noPermissionMessage = messagesManager.getMessageList("no-permission");
                    noPermissionMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
                    return false;
                }

                configManager.reloadConfig();
                messagesManager.reloadMessages();

                List<String> reloadMessages = messagesManager.getMessageList("config-reloaded");
                reloadMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
                break;

            case "setmessage":
                if (args.length < 2) {
                    List<String> usageMessage = messagesManager.getMessageList("setmessage-usage");
                    usageMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }
                String newMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                configManager.setJoinMessage(newMessage);
                List<String> successMessages = messagesManager.getMessageList("setmessage-success");
                successMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage( // 🔥 Исправлено!
                        msg.replace("%message%", newMessage)
                )));
                break;

            case "reset":
                if (!(sender instanceof Player player)) {
                    List<String> playerOnlyMessage = messagesManager.getMessageList("player-only");
                    playerOnlyMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                if (!permissionsManager.hasPermission(player, "xenjoin.reset")) {
                    List<String> noPermissionMessage = messagesManager.getMessageList("no-permission");
                    noPermissionMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                configManager.resetJoinMessage();

                List<String> resetSuccessMessage = messagesManager.getMessageList("reset-success");
                resetSuccessMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                break;

            case "menu":
                if (!(sender instanceof Player player)) {
                    List<String> playerOnlyMessage = messagesManager.getMessageList("player-only");
                    playerOnlyMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }
                player.openInventory(menuManager.createMainMenu());
                break;

            case "info":
                List<String> infoMessages = List.of(
                        "&7",
                        "&c&lXenVision &7— информация о плагине",
                        "&7Версия: &x&a&1&f&f&3&3&a1.0.4 &8BETA",
                        "&7Авторы: &x&e&b&c&2&3&0&eXenVision, okunivaxx",
                        "&7ТГК Разработчика: &x&0&a&b&3&f&f&t.me/xen_vision",
                        "&7"
                );
                infoMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
                break;

            case "ping":
                long startTime = System.nanoTime();
                Bukkit.getScheduler().runTask(configManager.getPlugin(), () -> {
                    long latency = (System.nanoTime() - startTime) / 1_000_000;
                    List<String> pingMessages = messagesManager.getMessageList("ping-result");
                    pingMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage( // 🔥 Исправлено!
                            msg.replace("%ping%", String.valueOf(latency))
                    )));
                });
                break;

            case "suggest":
                if (args.length < 2) {
                    List<String> usageMessage = messagesManager.getMessageList("suggest-usage");
                    usageMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                String suggestion = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                configManager.addSuggestedGreeting(suggestion);

                List<String> suggestMessages = messagesManager.getMessageList("suggest-success");
                suggestMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                break;

            case "list":
                List<String> greetings = configManager.getSuggestedGreetings();

                if (greetings.isEmpty()) {
                    List<String> emptyMessage = messagesManager.getMessageList("list-empty");
                    emptyMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
                    return true;
                }

                List<String> headerMessage = messagesManager.getMessageList("list-header");
                headerMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));

                for (int i = 0; i < greetings.size(); i++) {
                    final int index = i;
                    final int votes = configManager.getVotesForGreeting(i);
                    final String greetingMessage = greetings.get(i);

                    List<String> itemMessage = messagesManager.getMessageList("list-item");
                    itemMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(
                            msg.replace("%id%", String.valueOf(index))
                                    .replace("%message%", greetingMessage)
                                    .replace("%votes%", String.valueOf(votes))
                    )));
                }
                return true;

            case "vote":
                if (!(sender instanceof Player player)) {
                    List<String> playerOnlyMessage = messagesManager.getMessageList("player-only");
                    playerOnlyMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                if (args.length < 2) {
                    List<String> voteErrorMessage = messagesManager.getMessageList("vote-error");
                    voteErrorMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                try {
                    int voteId = Integer.parseInt(args[1]);
                    List<String> voteGreetings = configManager.getSuggestedGreetings();

                    if (voteId < 0 || voteId >= voteGreetings.size()) {
                        List<String> invalidMessage = messagesManager.getMessageList("vote-invalid");
                        invalidMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                        return false;
                    }

                    String playerKey = "votes." + player.getUniqueId();
                    if (configManager.getConfig().isSet(playerKey)) {
                        List<String> alreadyVotedMessage = messagesManager.getMessageList("vote-already-cast");
                        alreadyVotedMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                        return false;
                    }

                    int votes = configManager.getVotesForGreeting(voteId);
                    configManager.getConfig().set("votes." + voteId, votes + 1);
                    configManager.getConfig().set(playerKey, voteId);
                    configManager.saveConfig();

                    List<String> voteSuccessMessage = messagesManager.getMessageList("vote-success");
                    voteSuccessMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage( // 🔥 Исправлено!
                            msg.replace("%message%", voteGreetings.get(voteId))
                                    .replace("%votes%", String.valueOf(votes + 1))
                    )));

                } catch (NumberFormatException e) {
                    List<String> errorMessage = messagesManager.getMessageList("vote-error");
                    errorMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                }
                break;

            case "approve":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(messagesManager.getMessage("player-only"));
                    return false;
                }

                if (!permissionsManager.hasPermission(player, "xenjoin.approve")) {
                    sender.sendMessage(messagesManager.getMessage("no-permission"));
                    return false;
                }

                if (args.length < 2) {
                    List<String> usageMessage = messagesManager.getMessageList("approve-usage");
                    usageMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }

                try {
                    int approveId = Integer.parseInt(args[1]);
                    List<String> approveGreetings = configManager.getSuggestedGreetings();

                    if (approveId < 0 || approveId >= approveGreetings.size()) {
                        List<String> errorMessage = messagesManager.getMessageList("approve-error");
                        errorMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                        return false;
                    }

                    String approvedGreeting = approveGreetings.get(approveId);
                    configManager.getConfig().set("approved-greeting", approvedGreeting);
                    configManager.getConfig().set("approved-timestamp", System.currentTimeMillis());

                    configManager.getConfig().set("suggested-greetings", null);
                    configManager.getConfig().set("votes", null);
                    configManager.getConfig().set("voted-players", null);
                    configManager.saveConfig();

                    configManager.getConfig().set("join-message", approvedGreeting);
                    configManager.saveConfig();

                    List<String> successMessage = messagesManager.getMessageList("approve-success");

                    if (successMessage == null || successMessage.isEmpty()) {
                        sender.sendMessage(messagesManager.getMessage("approve-error"));
                        return false;
                    }
                    successMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage( // 🔥 Исправлено!
                            msg.replace("%message%", approvedGreeting)
                    )));

                } catch (NumberFormatException e) {
                    List<String> errorMessage = messagesManager.getMessageList("approve-error");
                    errorMessage.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg))); // 🔥 Исправлено!
                    return false;
                }
                return true;
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        List<String> helpMessages = messagesManager.getMessageList("help-message");
        helpMessages.forEach(msg -> sender.sendMessage(messagesManager.formatMessage(msg)));
    }
}