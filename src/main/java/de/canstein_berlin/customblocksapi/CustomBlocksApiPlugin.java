package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettingsBuilder;
import de.canstein_berlin.customblocksapi.commands.ConvertToPlaceBlockItem;
import de.canstein_berlin.customblocksapi.listener.BlockManageListener;
import de.canstein_berlin.customblocksapi.test.TestBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomBlocksApiPlugin extends JavaPlugin {

    public static TestBlock TEST_BLOCK;
    private static CustomBlocksApiPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        TEST_BLOCK = new TestBlock(BlockSettingsBuilder.empty()
                .withBaseBlock(Material.GLASS)
                .withDisplayMaterial(Material.STICK)
                .build()
                , 1);
        CustomBlocksApi.getInstance().register(new NamespacedKey("cba", "test_block"), TEST_BLOCK);

        //Test Commands
        getCommand("convertToPlaceable").setExecutor(new ConvertToPlaceBlockItem());

        Bukkit.getPluginManager().registerEvents(new BlockManageListener(), this);
    }

    public static CustomBlocksApiPlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
