package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.drops.IDrop;
import de.canstein_berlin.customblocksapi.api.block.drops.ItemDrop;
import de.canstein_berlin.customblocksapi.api.block.drops.LootTableDrop;
import de.canstein_berlin.customblocksapi.api.block.properties.*;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTable;
import de.canstein_berlin.customblocksapi.api.render.CMDLookupTableBuilder;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import de.canstein_berlin.customblocksapi.builder.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestBlock extends CustomBlock {

    public static BooleanProperty ENABLED;
    public static EnumProperty<BlockFace> FACING;
    public static IntProperty AGE0_5;

    static {
        ENABLED = Properties.ENABLED;
        FACING = Properties.FACING;
        AGE0_5 = new IntProperty("age", 0, 5);

    }

    public TestBlock(BlockSettings settings) {
        super(settings, 1, new ItemBuilder(Material.IRON_NUGGET).setCustomModelData(1).setDisplayName("§r§6" + settings.getName()).build());
        setDefaultState(getDefaultState().with(ENABLED, false).with(AGE0_5, 4).with(FACING, BlockFace.SOUTH));
    }


    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(ENABLED, FACING, AGE0_5);
    }

    @Override
    public CustomBlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerHorizontalLookDirection().getOppositeFace()).with(ENABLED, ctx.getPlacementPosition().getBlock().getBlockPower() > 0);
    }

    @Override
    public void onNeighborUpdate(CustomBlockState state, World world, Location location, CustomBlock block, Location fromPos) {
        state.with(ENABLED, location.getBlock().getBlockPower() > 0);
        state.update();
    }

    @Override
    public ActionResult onUse(CustomBlockState state, World world, Location location, Player player, EquipmentSlot hand) {
        player.sendMessage("You clicked me using your " + hand);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(CustomBlockState state, World world, Location location, @Nullable Player placer, ItemStack stack) {
        if (placer == null) return;
        placer.sendMessage("You placed me down. Thank you :)");
    }

    @Override
    public boolean onBreak(CustomBlockState state, World world, Location location, Player player) {
        player.sendMessage("NOoooo don't break me");
        return false;
    }

    @Override
    public void onDestroyedByExplosion(CustomBlockState state, World world, Location location) {
        Bukkit.broadcastMessage("I got exploded at location " + location.toBlockLocation());
    }

    @Override
    public void onSteppedOn(CustomBlockState state, World world, Location location, Entity entity) {
        Bukkit.broadcastMessage("Don't step on me!!! " + entity.getType());
    }

    @Override
    public List<IDrop> createDrops() {
        return List.of(
                ItemDrop.of(new ItemStack(Material.DIAMOND_BLOCK)),
                LootTableDrop.of(this, LootTables.DESERT_PYRAMID.getLootTable())
        );
    }

    @Override
    public CMDLookupTable createCMDLookupTable(CMDLookupTableBuilder tableBuilder) {
        tableBuilder.with(FACING, BlockFace.NORTH).with(ENABLED, false).hasCustomModelData(2).hasRotation(Axis.Y, 180).addElement();
        tableBuilder.with(FACING, BlockFace.SOUTH).with(ENABLED, false).hasCustomModelData(2).addElement();
        tableBuilder.with(FACING, BlockFace.WEST).with(ENABLED, false).hasCustomModelData(2).hasRotation(Axis.Y, 90).addElement();
        tableBuilder.with(FACING, BlockFace.EAST).with(ENABLED, false).hasCustomModelData(2).hasRotation(Axis.Y, 270).addElement();
        tableBuilder.with(FACING, BlockFace.NORTH).with(ENABLED, true).hasCustomModelData(3).hasRotation(Axis.Y, 180).addElement();
        tableBuilder.with(FACING, BlockFace.SOUTH).with(ENABLED, true).hasCustomModelData(3).addElement();
        tableBuilder.with(FACING, BlockFace.WEST).with(ENABLED, true).hasCustomModelData(3).hasRotation(Axis.Y, 90).addElement();
        tableBuilder.with(FACING, BlockFace.EAST).with(ENABLED, true).hasCustomModelData(3).hasRotation(Axis.Y, 270).addElement();
        return tableBuilder.build();
    }

}
