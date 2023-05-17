package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockEventListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().isSneaking()) return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getClickedBlock().getLocation());
        if (state == null) return;
        ActionResult result = state.getParentBlock().onUse(state, event.getClickedBlock().getWorld(), event.getClickedBlock().getLocation(), event.getPlayer(), event.getHand());
        if (result.isAccepted()) event.setCancelled(true);
    }

    @EventHandler
    public void onNeighborUpdate(BlockPhysicsEvent event) {
        if (!CustomBlocksApi.getInstance().usesNeighborUpdate()) return; // Performance
        if (!CustomBlocksApi.getInstance().getNeighborUpdateBlockMaterials().contains(event.getBlock().getType()))
            return; // Hopefully performance increase

        //Event Stuff
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;
        state.getParentBlock().onNeighborUpdate(state, event.getBlock().getWorld(), event.getBlock().getLocation().toBlockLocation(), state.getParentBlock(), event.getSourceBlock().getLocation().toBlockLocation());

    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if (!CustomBlocksApi.getInstance().usesEntityMovement()) return;
        if (!event.hasChangedPosition()) return;
        if (!CustomBlocksApi.getInstance().getEntityMovementBlockMaterials().contains(event.getEntity().getLocation().subtract(0, 1, 0).getBlock().getType()))
            return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getEntity().getLocation().subtract(0, 1, 0));
        if (state == null) return;
        state.getParentBlock().onSteppedOn(state, event.getEntity().getWorld(), event.getEntity().getLocation().subtract(0, 1, 0).toBlockLocation(), event.getEntity());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!CustomBlocksApi.getInstance().usesEntityMovement()) return;
        if (!event.hasChangedPosition()) return;
        if (!CustomBlocksApi.getInstance().getEntityMovementBlockMaterials().contains(event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType()))
            return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getPlayer().getLocation().subtract(0, 1, 0));
        if (state == null) return;
        state.getParentBlock().onSteppedOn(state, event.getPlayer().getWorld(), event.getPlayer().getLocation().subtract(0, 1, 0).toBlockLocation(), event.getPlayer());
    }

    @EventHandler
    public void disablePistonMovement(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void disablePistonMovement(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerUseNoBaseBlockBlock(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().isSneaking()) return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getRightClicked());
        if (state == null) return;
        ActionResult result = state.getParentBlock().onUse(state, event.getRightClicked().getWorld(), event.getRightClicked().getLocation(), event.getPlayer(), event.getHand());
        if (result.isAccepted()) event.setCancelled(true);
    }


}
