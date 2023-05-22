package de.canstein_berlin.customblocksapi.commands;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/*
PERMISSION: customblocks.commands.get
 */
public class GetCustomBlockCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPlayer")));
            return true;
        }
        Player player = ((Player) commandSender);
        if (!player.hasPermission("customblocks.commands.get")) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPermission")));
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.get.invalidCommand")));
            return true;
        }
        String stringKey = args[0];
        NamespacedKey key = NamespacedKey.fromString(stringKey);
        if (key == null || CustomBlocksApi.getInstance().getCustomBlock(key) == null) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.get.invalidKey", "key", stringKey)));
            return true;
        }

        CustomBlock block = CustomBlocksApi.getInstance().getCustomBlock(key);
        player.getInventory().addItem(block.getMainPlaceItemStack());
        commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.get.success", "block", block.getSettings().getName())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return CustomBlocksApi.getInstance().getAllCustomBlocks().stream()
                    .map(CustomBlock::getKey)
                    .map(NamespacedKey::asString)
                    .sorted()
                    .collect(Collectors.toList());
        }
        return List.of();

    }
}
