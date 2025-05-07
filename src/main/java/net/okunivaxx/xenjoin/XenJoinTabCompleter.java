package net.okunivaxx.xenjoin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class XenJoinTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Предоставляем список основных подкоманд
            suggestions.add("reload");
            suggestions.add("setmessage");
            suggestions.add("reset");
            suggestions.add("info");
            suggestions.add("ping");
            suggestions.add("menu");
            suggestions.add("list");
            suggestions.add("vote");
            suggestions.add("suggest");
            suggestions.add("approve");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setmessage")) {
                suggestions.add("<кастомное приветствие>");
            }
        }
        List<String> result = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                result.add(suggestion);
            }
        }

        return result;
    }
}
