package de.canstein_berlin.customblocksapi.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockType;
import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.gui.ListCustomBlocksGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ReplaceCustomBlocksCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPlayer")));
            return true;
        }
        if (!commandSender.hasPermission("customblocks.commands.replace")) {
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.noPermission")));
            return true;
        }

        if(strings.length != 2){
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.replace.invalidCommand")));
            return true;
        }

        Material material = Material.getMaterial(strings[0]);
        if(material == null){
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.replace.invalidMaterial", "material", strings[0])));
            return true;
        }

        NamespacedKey key = NamespacedKey.fromString(strings[1]);
        if(key == null){
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.replace.invalidKey", "key", strings[1])));
            return true;
        }

        CustomBlock block = CustomBlocksApi.getInstance().getCustomBlock(key);
        if(block == null){
            commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.replace.invalidKey", "key", strings[1])));
            return true;
        }

        Player player = ((Player) commandSender);
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);

        BlockType type = BukkitAdapter.asBlockType(material);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(player.getWorld());

        World paperWorld = player.getWorld();

        ArrayList<Vector3> blocks = new ArrayList<>();
        long start = System.currentTimeMillis();


        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (BlockVector3 current : WorldEdit.getInstance().getSessionManager().get(bukkitPlayer).getSelection()) {
                        if (world.getBlock(current).getBlockType() == type) {
                            blocks.add(current.toVector3());
                        }
                    }
                } catch (IncompleteRegionException e) {
                    e.printStackTrace();
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(Vector3 vector3 : blocks){
                            Location location = BukkitAdapter.adapt(paperWorld, vector3);
                            CustomBlocksApi.getInstance().setBlock(location, block);
                        }

                        commandSender.sendMessage(Component.text(CustomBlocksApiPlugin.getPrefixedLang("lang.commands.replace.success", "amount", String.valueOf(blocks.size()), "seconds", String.valueOf((System.currentTimeMillis() - start) / 1000f))));
                    }
                }.runTask(CustomBlocksApiPlugin.getInstance());
            }
        }.runTaskAsynchronously(CustomBlocksApiPlugin.getInstance());


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Arrays.stream(Material.values())
                    .map(Material::name)
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (strings.length == 2) {
            return CustomBlocksApi.getInstance().getAllCustomBlocks().stream()
                    .map(CustomBlock::getKey)
                    .map(NamespacedKey::asString)
                    .sorted()
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
