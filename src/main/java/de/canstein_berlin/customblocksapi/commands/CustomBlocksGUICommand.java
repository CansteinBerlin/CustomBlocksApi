package de.canstein_berlin.customblocksapi.commands;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.gui.ListCustomBlocksGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomBlocksGUICommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPlayer"));
            return true;
        }

        Player player = ((Player) commandSender);
        new ListCustomBlocksGUI().show(player);
        player.sendMessage(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.list.success"));
        return true;
    }
}
