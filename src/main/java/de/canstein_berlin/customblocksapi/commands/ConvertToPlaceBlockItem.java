package de.canstein_berlin.customblocksapi.commands;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConvertToPlaceBlockItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            ItemStack stack = player.getItemInHand();
            if (!stack.getType().isAir()) {
                CustomBlocksApiPlugin.TEST_BLOCK_HIGHER.toPlaceItemStack(stack);
            }
        }
        return false;
    }
}
