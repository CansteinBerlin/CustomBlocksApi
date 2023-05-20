package de.canstein_berlin.customblocksapi.commands;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestPlacementCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        Player player = ((Player) commandSender);
        Location location = player.getLocation().toBlockLocation();

        for (int x = 0; x < 50; x++) {
            for (int z = 0; z < 50; z++) {
                CustomBlocksApi.getInstance().setBlock(location.clone().add(x, 0, z), CustomBlocksApiPlugin.TEST_BLOCK);
            }
        }

        return true;
    }
}
