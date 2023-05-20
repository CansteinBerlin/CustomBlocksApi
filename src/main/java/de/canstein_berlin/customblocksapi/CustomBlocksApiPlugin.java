package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettingsBuilder;
import de.canstein_berlin.customblocksapi.api_listener.BlockEventListener;
import de.canstein_berlin.customblocksapi.api_listener.BlockManageListener;
import de.canstein_berlin.customblocksapi.commands.ConvertToPlaceBlockItem;
import de.canstein_berlin.customblocksapi.commands.TestPlacementCommand;
import de.canstein_berlin.customblocksapi.test.TestBlock;
import de.canstein_berlin.customblocksapi.test.TestBlockHigher;
import de.canstein_berlin.customblocksapi.test.TestBlockNoBaseBlock;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class CustomBlocksApiPlugin extends JavaPlugin {

    public static TestBlock TEST_BLOCK;
    public static TestBlockNoBaseBlock TEST_BLOCK_NO_BASE_BLOCK;
    private static CustomBlocksApiPlugin instance;
    public static TestBlockHigher TEST_BLOCK_HIGHER;

    @Override
    public void onEnable() {
        instance = this;
        TEST_BLOCK = new TestBlock(BlockSettingsBuilder.empty()
                .baseBlock(Material.GLASS)
                .displayMaterial(Material.STICK)
                .neighborUpdate(true)
                .entityMovement(true)
                .customName("Test Block")
                .dropsWhenExploded(true)
                .widthSound(Sound.BLOCK_AMETHYST_BLOCK_PLACE, Sound.BLOCK_AMETHYST_BLOCK_BREAK)
                .build()
        );

        TEST_BLOCK_NO_BASE_BLOCK = new TestBlockNoBaseBlock(BlockSettingsBuilder.empty()
                .displayMaterial(Material.STICK)
                .customName("No base block")
                .noBaseBlock(true)
                .neighborUpdate(true)
                .widthSound(Sound.BLOCK_WOOD_PLACE, Sound.BLOCK_WOOD_BREAK)
                .build());

        TEST_BLOCK_HIGHER = new TestBlockHigher(BlockSettingsBuilder.empty()
                .displayMaterial(Material.STICK)
                .customName("Higher Block")
                .noBaseBlock(true)
                .size(5f, 5f)
                .widthSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_SWEET_BERRY_BUSH_BREAK)
                .build()
        );

        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block"), TEST_BLOCK);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block_no_base"), TEST_BLOCK_NO_BASE_BLOCK);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block_higher"), TEST_BLOCK_HIGHER);

        //Test Commands
        getCommand("convertToPlaceable").setExecutor(new ConvertToPlaceBlockItem());
        getCommand("testplacement").setExecutor(new TestPlacementCommand());

        Bukkit.getPluginManager().registerEvents(new BlockManageListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(Component.text((int) Bukkit.getAverageTickTime()));
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    public static CustomBlocksApiPlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
