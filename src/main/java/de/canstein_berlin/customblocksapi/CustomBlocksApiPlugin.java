package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettingsBuilder;
import de.canstein_berlin.customblocksapi.api_listener.BlockEventListener;
import de.canstein_berlin.customblocksapi.api_listener.BlockManageListener;
import de.canstein_berlin.customblocksapi.commands.CustomBlocksGUICommand;
import de.canstein_berlin.customblocksapi.commands.GetCustomBlockCommand;
import de.canstein_berlin.customblocksapi.test.TestBlock;
import de.canstein_berlin.customblocksapi.test.TestBlockHigher;
import de.canstein_berlin.customblocksapi.test.TestBlockNoBaseBlock;
import de.canstein_berlin.customblocksapi.test.TestBlockTickable;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class CustomBlocksApiPlugin extends JavaPlugin {

    public static String PREFIX;
    public static TestBlock TEST_BLOCK;
    public static TestBlockNoBaseBlock TEST_BLOCK_NO_BASE_BLOCK;
    private static CustomBlocksApiPlugin instance;
    public static TestBlockHigher TEST_BLOCK_HIGHER;
    public static TestBlockTickable TEST_BLOCK_TICKABLE;

    @Override
    public void onEnable() {
        instance = this;

        //Set up config
        saveResource("config.yml", false);
        PREFIX = getLang("prefix");

        TEST_BLOCK = new TestBlock(BlockSettingsBuilder.empty()
                .baseBlock(Material.GLASS)
                .displayMaterial(Material.STICK)
                .neighborUpdate(true)
                .entityMovement(true)
                .customName("Test Block")
                .dropsWhenExploded(true)
                .withSound(Sound.BLOCK_AMETHYST_BLOCK_PLACE, Sound.BLOCK_AMETHYST_BLOCK_BREAK)
                .breakInstantly(true)
                .build()
        );

        TEST_BLOCK_NO_BASE_BLOCK = new TestBlockNoBaseBlock(BlockSettingsBuilder.empty()
                .displayMaterial(Material.STICK)
                .customName("No base block")
                .noBaseBlock(true)
                .neighborUpdate(true)
                .withSound(Sound.BLOCK_WOOD_PLACE, Sound.BLOCK_WOOD_BREAK)
                .build());

        TEST_BLOCK_HIGHER = new TestBlockHigher(BlockSettingsBuilder.empty()
                .displayMaterial(Material.STICK)
                .baseBlock(Material.STONE)
                .customName("Higher Block")
                .noBaseBlock(true)
                .size(1f, 1.5f)
                .withSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_SWEET_BERRY_BUSH_BREAK)
                .build()
        );

        TEST_BLOCK_TICKABLE = new TestBlockTickable(BlockSettingsBuilder.empty()
                .displayMaterial(Material.STICK)
                .customName("Tickable")
                .withSound(Sound.BLOCK_FROGLIGHT_PLACE, Sound.BLOCK_SHROOMLIGHT_BREAK)
                .neighborUpdate(true)
                .build());

        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block"), TEST_BLOCK);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block_no_base"), TEST_BLOCK_NO_BASE_BLOCK);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block_higher"), TEST_BLOCK_HIGHER);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block_tickable"), TEST_BLOCK_TICKABLE);

        //Test Commands
        getCommand("listCustomBlocks").setExecutor(new CustomBlocksGUICommand());
        getCommand("getCustomBlock").setExecutor(new GetCustomBlockCommand());
        getCommand("getCustomBlock").setTabCompleter(new GetCustomBlockCommand());

        //Register Api Listeners
        Bukkit.getPluginManager().registerEvents(new BlockManageListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(Component.text("Mspt: " + (int) Bukkit.getAverageTickTime() + "; Cached Blocks: " + CustomBlocksApi.getInstance().cachedBlocks()));
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    public static CustomBlocksApiPlugin getInstance() {
        return instance;
    }

    public static String getLang(String key, String... args) {
        String lang = CustomBlocksApiPlugin.getInstance().getConfig().getString(key, "&cUnknown language key &6" + key);
        for (int i = 0; i + 1 < args.length; i += 2) {
            lang = lang.replace("%" + args[i] + "%", args[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', lang);
    }

    public static String getPrefixedLang(String key, String... args) {
        return PREFIX + getLang(key, args);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
