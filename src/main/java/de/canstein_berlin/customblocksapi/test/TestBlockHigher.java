package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.BooleanProperty;
import de.canstein_berlin.customblocksapi.api.block.properties.Properties;
import de.canstein_berlin.customblocksapi.api.block.properties.PropertyListBuilder;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTable;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableBuilder;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TestBlockHigher extends CustomBlock {

    public static BooleanProperty BERRIES;

    static {
        BERRIES = Properties.BERRIES;
    }

    public TestBlockHigher(BlockSettings settings) {
        super(settings, 4);
        setDefaultState(getDefaultState().with(BERRIES, false));
    }

    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(BERRIES);
    }

    @Override
    public CustomBlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(BERRIES, true);
    }

    @Override
    public ActionResult onUse(CustomBlockState state, World world, Location location, Player player, EquipmentSlot hand) {
        if (state.get(BERRIES) == false) {
            return ActionResult.SUCCESS;
        }
        state.with(BERRIES, false);
        state.update();

        //DropItem
        ItemStack stack = new ItemStack(Material.SWEET_BERRIES);
        stack.setAmount(blockRandom.nextInt(2) + 1);
        world.dropItem(location.add(settings.getWidth() / 2, 0, settings.getWidth() / 2), stack);

        //Regrow
        new BukkitRunnable() {

            @Override
            public void run() {
                state.with(BERRIES, true);
                state.update();
            }
        }.runTaskLater(CustomBlocksApiPlugin.getInstance(), blockRandom.nextInt(60) + 40);

        return ActionResult.SUCCESS;
    }

    @Override
    public void applyInitialModelTransformations(ItemDisplay display) {
        display.setTransformation(new Transformation(new Vector3f((getBlockRandom().nextFloat() - 0.5f) / 3f, 0, (getBlockRandom().nextFloat() - 0.5f) / 3f), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf()));
        display.setRotation(blockRandom.nextInt(360), 0);
    }

    @Override
    public CMDLookupTable createCMDLookupTable(CMDLookupTableBuilder tableBuilder) {
        return tableBuilder.with(BERRIES, false).hasCustomModelData(5).addElement()
                .with(BERRIES, true).hasCustomModelData(4).addElement().build();
    }
}
