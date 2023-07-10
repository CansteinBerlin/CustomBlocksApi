package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api_listener.BlockEventListener;
import de.canstein_berlin.customblocksapi.api_listener.BlockManageListener;
import de.canstein_berlin.customblocksapi.commands.CustomBlocksGUICommand;
import de.canstein_berlin.customblocksapi.commands.GetCustomBlockCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class CustomBlocksApiPlugin extends JavaPlugin {

    public static String PREFIX;

    private static CustomBlocksApiPlugin instance;


    @Override
    public void onEnable() {
        instance = this;

        //Set up config
        saveResource("config.yml", false);
        PREFIX = getLang("prefix");

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
