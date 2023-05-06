package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

public class BlockManageListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return; //Item Interaction has a higher priority
        if (!event.getItemInHand().getItemMeta().getPersistentDataContainer().has(CustomBlock.CUSTOM_BLOCK_KEY)) return;
        event.setCancelled(true);
        String stringifiedNameSpacedKey = event.getItemInHand().getItemMeta().getPersistentDataContainer().get(CustomBlock.CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        if (stringifiedNameSpacedKey == null) return;
        NamespacedKey namespacedKey = NamespacedKey.fromString(stringifiedNameSpacedKey);

        //Place Block
        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(namespacedKey);
        customBlock.create(event.getBlock().getLocation());
    }

}
