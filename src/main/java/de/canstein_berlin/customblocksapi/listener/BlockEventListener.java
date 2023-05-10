package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.Collection;

import static de.canstein_berlin.customblocksapi.listener.BlockManageListener.getKeyFromPersistentDataContainer;

public class BlockEventListener implements Listener {

    @EventHandler
    public void onNeighborUpdate(BlockPhysicsEvent event) {
        if (!CustomBlocksApi.getInstance().usesNeighborUpdate()) return; // Performance
        if (!CustomBlocksApi.getInstance().getCustomBlockMaterials().contains(event.getBlock().getType()))
            return; // Hopefully performance increase

        Collection<ItemDisplay> displays = event.getBlock().getLocation().toBlockLocation().add(0.5, 0.5, 0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.1);
        if (displays.size() == 0) return;
        CustomBlock block = null;
        ItemDisplay display = null;
        for (ItemDisplay d : displays) {
            if (d.getPersistentDataContainer().has(CustomBlock.CUSTOM_BLOCK_KEY)) {
                NamespacedKey key = getKeyFromPersistentDataContainer(d.getPersistentDataContainer());
                if (key == null) continue;
                block = CustomBlocksApi.getInstance().getCustomBlock(key);
                if (block != null) {
                    display = d;
                    break;
                }
            }
        }
        if (block == null) return;
        CustomBlockState state = new CustomBlockState(block, display);
        block.onNeighborUpdate(state, event.getBlock().getWorld(), event.getBlock().getLocation().toBlockLocation(), block, event.getSourceBlock().getLocation().toBlockLocation());

    }


}
