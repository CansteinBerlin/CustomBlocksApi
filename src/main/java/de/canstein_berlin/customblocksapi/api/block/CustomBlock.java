package de.canstein_berlin.customblocksapi.api.block;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import de.canstein_berlin.customblocksapi.api.block.properties.PropertyListBuilder;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomBlock {

    //Identifier to save the identifier of the block to an item or the displayEntity
    public static final NamespacedKey CUSTOM_BLOCK_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "custom_block");


    private NamespacedKey key; // Internal identifier of the block
    private BlockSettings settings; //Block's Settings that "control" the block
    private int customModelData; // Custom Model Data of the Block will be replaced later
    private ImmutableMap<String, Property<?>> properties;


    public CustomBlock(BlockSettings settings, int customModelData) {
        this.settings = settings;
        this.customModelData = customModelData;

        final PropertyListBuilder listBuilder = new PropertyListBuilder(this);
        appendProperties(listBuilder);
        properties = ImmutableMap.copyOf(listBuilder.build());
    }

    /**
     * If you want to add new properties to your block they should be added in this method.
     *
     * @param propertyListBuilder PropertyListBuilder to build the property list;
     * @return
     */
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
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
        meta.setCustomModelData(customModelData);
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
        final Location loc = location.toBlockLocation();

        //Spawn Display Entity
        loc.getWorld().spawn(loc.clone().add(0.5, 0.5, 0.5), ItemDisplay.class, (entity) -> {
            ItemStack stack = new ItemStack(settings.getDisplayMaterial());
            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(customModelData);
            stack.setItemMeta(meta);

            entity.setItemStack(stack);
            entity.getPersistentDataContainer().set(CUSTOM_BLOCK_KEY, PersistentDataType.STRING, key.asString());
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
        });

        //Set Block
        loc.getBlock().setType(settings.getBaseBlock());
    }

    /**
     * Called when a block should be removed
     *
     * @param validDisplay Underlying ItemDisplay
     * @param loc          Location of the block
     */
    public void remove(ItemDisplay validDisplay, Location loc) {
        validDisplay.remove();
        loc.getBlock().setType(Material.AIR);
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

    public int getCustomModelData() {
        return customModelData;
    }

    public ImmutableMap<String, Property<?>> getProperties() {
        return properties;
    }
}
