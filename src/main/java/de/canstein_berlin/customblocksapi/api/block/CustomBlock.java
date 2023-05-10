package de.canstein_berlin.customblocksapi.api.block;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import de.canstein_berlin.customblocksapi.api.block.properties.PropertyListBuilder;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTable;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableBuilder;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableElement;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class CustomBlock {

    //Identifier to save the identifier of the block to an item or the displayEntity
    public static final NamespacedKey CUSTOM_BLOCK_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "custom_block");


    private NamespacedKey key; // Internal identifier of the block
    private final BlockSettings settings; //Block's Settings that "control" the block
    private CMDLookupTable customModelDataLookupTable;
    private final ImmutableMap<String, Property<?>> properties; // Properties of the generic block

    private CustomBlockState defaultState;
    private int customModelDataDefault;

    public CustomBlock(BlockSettings settings, int customModelDataDefault) {
        this.settings = settings;
        this.customModelDataDefault = customModelDataDefault;

        //Add Properties
        final PropertyListBuilder listBuilder = new PropertyListBuilder(this);
        appendProperties(listBuilder);
        properties = ImmutableMap.copyOf(listBuilder.build());

        //Define default state can be changed later
        defaultState = new CustomBlockState(this, properties);

        //Custom Model Data Lookup Table
        customModelDataLookupTable = createCMDLookupTable(new CMDLookupTableBuilder(this));
    }

    /**
     * If you want to add new properties to your block they should be added in this method.
     *
     * @param propertyListBuilder PropertyListBuilder to build the property list;
     * @return
     */
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
    }

    public void setDefaultState(CustomBlockState defaultState) {
        this.defaultState = defaultState;
    }

    public CMDLookupTable createCMDLookupTable(CMDLookupTableBuilder tableBuilder) {
        return tableBuilder.build();
    }

    /**
     * Method to add the identifier of the block to an item. Used for the place Item
     *
     * @param stack ItemsStack to be converted
     * @return Converted ItemStack
     */
    public ItemStack toPlaceItemStack(ItemStack stack) {
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
    public void create(ItemPlacementContext ctx) {
        final Location loc = ctx.getPlacementPosition().toBlockLocation();

        //Spawn Display Entity
        ItemDisplay display = loc.getWorld().spawn(loc.clone().add(0.5, 0.5, 0.5), ItemDisplay.class, (entity) -> {
            //Entity Setup
            entity.getPersistentDataContainer().set(CUSTOM_BLOCK_KEY, PersistentDataType.STRING, key.asString());
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            entity.setViewRange(10);

            //Save default State to entity
            CustomBlockState state = getPlacementState(ctx);
            state.saveToEntity(entity);
            new CustomBlockState(this, entity);

            CMDLookupTableElement element = customModelDataLookupTable.match(state);

            //Set Display Item
            ItemStack stack = new ItemStack(settings.getDisplayMaterial());
            ItemMeta meta = stack.getItemMeta();
            if (element == null) meta.setCustomModelData(customModelDataDefault);
            else meta.setCustomModelData(element.getCustomModelData());
            stack.setItemMeta(meta);
            entity.setItemStack(stack);

            //Rotate Item According to the element
            if (element == null || element.getRotations().isEmpty()) return;
            int xRotation = 0;
            int yRotation = 0;
            for (Map.Entry<Axis, Integer> entry : element.getRotations().entrySet()) {
                if (entry.getKey() == Axis.Y) xRotation = entry.getValue();
                if (entry.getKey() == Axis.X) yRotation = entry.getValue();
            }
            entity.setRotation(xRotation, yRotation);
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

    /**
     * This method should be overridden if you want to change the blockstates in regard to the player placing the block
     *
     * @param ctx ItemPlacementContext that holds information about how the block was placed
     * @return Blockstate that is placed in the world
     */
    public CustomBlockState getPlacementState(ItemPlacementContext ctx) {
        return defaultState;
    }

    public void onNeighborUpdate(CustomBlockState state, World world, Location location, CustomBlock block, Location fromPos) {

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

    public ImmutableMap<String, Property<?>> getProperties() {
        return properties;
    }

    public CustomBlockState getDefaultState() {
        return defaultState;
    }

    public void redraw(CustomBlockState state, ItemDisplay display) {
        CMDLookupTableElement element = customModelDataLookupTable.match(state);

        //Set Display Item
        ItemStack stack = new ItemStack(settings.getDisplayMaterial());
        ItemMeta meta = stack.getItemMeta();
        if (element == null) meta.setCustomModelData(customModelDataDefault);
        else meta.setCustomModelData(element.getCustomModelData());
        stack.setItemMeta(meta);
        display.setItemStack(stack);
    }
}
