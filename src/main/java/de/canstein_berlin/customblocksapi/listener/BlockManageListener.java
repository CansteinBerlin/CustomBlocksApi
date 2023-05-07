package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class BlockManageListener implements Listener {

    /**
     * Disable the placement of blocks when interacting with a block place item and an interactable block like tnt
     *
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NamespacedKey key = getKeyFromPersistentDataContainer(event.getItemInHand().getItemMeta().getPersistentDataContainer());
        if (key == null) return;

        CustomBlock block = CustomBlocksApi.getInstance().getCustomBlock(key);
        if (block == null) return;

        event.setCancelled(true);
    }

    /**
     * Event to listen to the placement of blocks
     *
     * @param event
     */
    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return; // Ignore Left Click
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getItem() == null) return; // Check if item not air
        if (event.getInteractionPoint() == null) return; // Check if clicked on Block
        if (event.getClickedBlock().getType().isInteractable() && !event.getPlayer().isSneaking()) return;

        //Get Key from Item
        NamespacedKey key = getKeyFromPersistentDataContainer(event.getItem().getItemMeta().getPersistentDataContainer());
        if (key == null) return;

        //Cancel Event
        event.setCancelled(true);

        //Get and verify location
        Location placeLocation;
        if (event.getClickedBlock().isReplaceable()) placeLocation = event.getClickedBlock().getLocation();
        else placeLocation = event.getClickedBlock().getLocation().add(event.getBlockFace().getDirection());

        if (!placeLocation.getBlock().isReplaceable()) return; // Only replace replaceable blocks like air or grass
        Collection<Entity> collided = placeLocation.clone().add(0.5, 0.5, 0.5).getNearbyEntities(0.5, 0.5, 0.5); // Get entities colliding with the "new Block"
        for (Entity e : collided) { // Ignore Items
            if (!(e instanceof Item)) return;
        }

        //Place Block
        placeBlockInWorld(key, placeLocation);

        //Remove Item
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) // Infinite Items in GameMode
            return;

        //Use Item
        ItemStack stack = event.getItem();
        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() <= 0) event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
    }

    /**
     * Event to listen to the breaking of blocks
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Collection<ItemDisplay> displays = loc.clone().add(0.5, 0.5, 0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.1);
        if (displays.size() == 0) return;
        CustomBlock block = null;
        ItemDisplay validDisplay = null;
        for (ItemDisplay display : displays) {
            if (display.getPersistentDataContainer().has(CustomBlock.CUSTOM_BLOCK_KEY)) {
                NamespacedKey key = getKeyFromPersistentDataContainer(display.getPersistentDataContainer());
                if (key == null) continue;
                block = CustomBlocksApi.getInstance().getCustomBlock(key);
                if (block != null) {
                    validDisplay = display;
                    break;
                }
            }
        }
        if (block == null) return;

        block.remove(validDisplay, loc);

    }

    private void placeBlockInWorld(NamespacedKey key, Location loc) {
        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        customBlock.create(loc);
    }

    private NamespacedKey getKeyFromPersistentDataContainer(PersistentDataContainer persistentDataContainer) {
        if (!persistentDataContainer.has(CustomBlock.CUSTOM_BLOCK_KEY)) return null;
        String stringifiedNameSpacedKey = persistentDataContainer.get(CustomBlock.CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        if (stringifiedNameSpacedKey == null) return null;
        return NamespacedKey.fromString(stringifiedNameSpacedKey);
    }

}
