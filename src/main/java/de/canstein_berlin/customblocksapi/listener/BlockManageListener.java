package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class BlockManageListener implements Listener {

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return; // Ignore Left Click
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getItem() == null) return; // Check if item not air
        if (event.getInteractionPoint() == null) return; // Check if clicked on Block

        //Get Key from Item
        NamespacedKey key = getKeyFromItem(event.getItem());
        if (key == null) return;
        event.setCancelled(true);

        //Get and Verify location
        Location placeLocation = event.getInteractionPoint().add(event.getPlayer().getEyeLocation().getDirection().multiply(-0.01)).toBlockLocation();
        if (!placeLocation.getBlock().isReplaceable()) return; // Only replace replaceable blocks like air or grass
        Collection<Entity> collided = placeLocation.clone().add(0.5, 0.5, 0.5).getNearbyEntities(0.5, 0.5, 0.5); // Get entities colliding with the "new Block"
        for (Entity e : collided) { // Ignore Items
            if (!(e instanceof Item)) return;
        }

        //Place Block
        placeBlockInWorld(key, placeLocation);

    }

    private void placeBlockInWorld(NamespacedKey key, Location loc) {
        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        customBlock.create(loc);
    }

    private NamespacedKey getKeyFromItem(ItemStack stack) {
        if (!stack.getItemMeta().getPersistentDataContainer().has(CustomBlock.CUSTOM_BLOCK_KEY)) return null;
        String stringifiedNameSpacedKey = stack.getItemMeta().getPersistentDataContainer().get(CustomBlock.CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        if (stringifiedNameSpacedKey == null) return null;
        return NamespacedKey.fromString(stringifiedNameSpacedKey);
    }

}
