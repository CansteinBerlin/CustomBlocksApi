package de.canstein_berlin.customblocksapi.api.block;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import de.canstein_berlin.customblocksapi.api.block.properties.PropertyListBuilder;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTable;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableBuilder;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableElement;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import de.canstein_berlin.customblocksapi.test.TestBlock;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
        defaultState = setDefaultState(defaultState);

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

    public CustomBlockState setDefaultState(CustomBlockState defaultState) {
        return defaultState;
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
        final Location loc = location.toBlockLocation();

        //Spawn Display Entity
        ItemDisplay display = loc.getWorld().spawn(loc.clone().add(0.5, 0.5, 0.5), ItemDisplay.class, (entity) -> {
            //Entity Setup
            entity.getPersistentDataContainer().set(CUSTOM_BLOCK_KEY, PersistentDataType.STRING, key.asString());
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            entity.setViewRange(2);

            //Save default State to entity
            Random random = new Random();
            defaultState.with(TestBlock.FACING, List.of(BlockFace.SOUTH, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST).get(random.nextInt(4)));
            defaultState.with(TestBlock.ENABLED, random.nextBoolean());
            defaultState.saveToEntity(entity);
            new CustomBlockState(this, entity);

            CMDLookupTableElement element = customModelDataLookupTable.match(defaultState);

            //Set Display Item
            ItemStack stack = new ItemStack(settings.getDisplayMaterial());
            ItemMeta meta = stack.getItemMeta();
            if (element == null) meta.setCustomModelData(customModelDataDefault);
            else meta.setCustomModelData(element.getCustomModelData());
            stack.setItemMeta(meta);
            entity.setItemStack(stack);

            //Rotate Item According to the element
            if (element == null || element.getRotations().isEmpty()) return;
            System.out.println(element.getRotations());
            final int[] iterator = {0};
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
}
