package de.canstein_berlin.customblocksapi.api.block;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomBlock {

    //Identifier to save the identifier of the block to an item or the displayEntity
    public static final NamespacedKey CUSTOM_BLOCK_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "custom_block");


    private NamespacedKey key; // Internal identifier of the block
    private BlockSettings settings; //Block's Settings that "control" the block

    public CustomBlock(BlockSettings settings) {
        this.settings = settings;
    }


    /**
     * Method to add the identifier of the block to an item. Used for the place Item
     *
     * @param stack ItemsStack to be converted
     * @return Converted ItemStack
     */
    public ItemStack toPlaceItemStack(ItemStack stack) {
        System.out.println("Converted");
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(CUSTOM_BLOCK_KEY, PersistentDataType.STRING, key.asString());
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Method that created the block in the world at a specific location.
     *
     * @param location Location where to create the block.
     */
    public void create(Location location) {
        new BukkitRunnable() {

            @Override
            public void run() {
                location.getBlock().setType(settings.getBaseBlock());
            }
        }.runTaskLater(CustomBlocksApiPlugin.getInstance(), 1);
    }

    public NamespacedKey getKey() {
        return key;
    }

    public BlockSettings getSettings() {
        return settings;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }
}
