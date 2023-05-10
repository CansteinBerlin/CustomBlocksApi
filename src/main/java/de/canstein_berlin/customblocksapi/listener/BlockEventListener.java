package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockEventListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().isSneaking()) return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getClickedBlock().getLocation());
        if (state == null) return;
        ActionResult result = state.getParentBlock().onUse(state, event.getClickedBlock().getWorld(), event.getClickedBlock().getLocation(), event.getPlayer(), event.getHand());
        if (result == ActionResult.SUCCESS) event.setCancelled(true);
    }

    @EventHandler
    public void onNeighborUpdate(BlockPhysicsEvent event) {
        if (!CustomBlocksApi.getInstance().usesNeighborUpdate()) return; // Performance
        if (!CustomBlocksApi.getInstance().getCustomBlockMaterials().contains(event.getBlock().getType()))
            return; // Hopefully performance increase

        //Event Stuff
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;
        state.getParentBlock().onNeighborUpdate(state, event.getBlock().getWorld(), event.getBlock().getLocation().toBlockLocation(), state.getParentBlock(), event.getSourceBlock().getLocation().toBlockLocation());

    }


}
