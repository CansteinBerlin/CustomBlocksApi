package de.canstein_berlin.customblocksapi.commands;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.gui.ListCustomBlocksGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/*
Permission: customblocks.commands.gui
 */
public class CustomBlocksGUICommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPlayer")));
            return true;
        }
        if (!commandSender.hasPermission("customblocks.commands.gui")) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPermission")));
            return true;
        }

        Player player = ((Player) commandSender);
        new ListCustomBlocksGUI().show(player);
        player.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.list.success")));
        return true;
    }
}
