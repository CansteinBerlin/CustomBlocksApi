package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TestBlockNoBaseBlock extends CustomBlock {

    public TestBlockNoBaseBlock(BlockSettings settings) {
        super(settings, 1);
    }


    @Override
    public void onPlaced(CustomBlockState state, World world, Location location, @Nullable Player placer, ItemStack stack) {
        if (placer == null) return;
        placer.sendMessage("You placed me thank you!");
    }

    @Override
    public boolean onBreak(CustomBlockState state, World world, Location location, Player player) {
        player.sendMessage("You broke me :(");
        return !player.isSneaking();
    }

    @Override
    public void onDestroyedByExplosion(CustomBlockState state, World world, Location location) {
        Bukkit.broadcastMessage("NOOO EXPLOSION!!!");
    }

    @Override
    public ActionResult onUse(CustomBlockState state, World world, Location location, Player player, EquipmentSlot hand) {
        player.sendMessage("Hmm i like it");
        return ActionResult.SUCCESS;
    }

    @Override
    public CustomBlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState();
        //return ctx.getPlacementPosition().subtract(0, 1, 0).getBlock().getType().equals(Material.FARMLAND) ? getDefaultState() : null;
    }

    @Override
    public void onNeighborUpdate(CustomBlockState state, World world, Location location, CustomBlock block, Location fromPos) {
        Bukkit.broadcastMessage("Updated " + settings.getName());
    }
}
