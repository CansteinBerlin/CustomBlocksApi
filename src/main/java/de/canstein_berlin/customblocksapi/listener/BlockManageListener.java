package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockManageListener implements Listener {

    /**
     * Disable the placement of blocks when interacting with a block place item and an interactable block like tnt
     *
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(event.getItemInHand().getItemMeta().getPersistentDataContainer());
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
        if (event.isCancelled()) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return; // Ignore Left Click
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getItem() == null) return; // Check if item not air
        if (event.getInteractionPoint() == null) return; // Check if clicked on Block
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().isInteractable() && !event.getPlayer().isSneaking()) return;

        //Get Key from Item
        NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(event.getItem().getItemMeta().getPersistentDataContainer());
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

        //Create ItemPlacement Context
        ItemPlacementContext context = new ItemPlacementContext(event.getPlayer(), event.getHand(), placeLocation, event.getClickedBlock().isReplaceable(), event.getBlockFace());

        //Place Block
        placeBlockInWorld(key, context); // Item Placement Context creation

        //Infinite items if player is in creative or spectator
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) // Infinite Items in GameMode
            return;

        //Reduce Item stack
        ItemStack stack = event.getItem();
        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() <= 0)
            event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));

    }

    /**
     * Event to listen to the breaking of blocks
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;

        //Remove Block
        state.getParentBlock().onBreak(state, event.getBlock().getWorld(), event.getBlock().getLocation(), event.getPlayer());
        state.remove(event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;

            state.getParentBlock().onDestroyedByExplosion(state, event.getBlock().getWorld(), block.getLocation());
            state.remove(block.getLocation());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;

            state.getParentBlock().onDestroyedByExplosion(state, block.getWorld(), block.getLocation());
            state.remove(block.getLocation());
        }
    }

    private void placeBlockInWorld(NamespacedKey key, ItemPlacementContext ctx) {
        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        customBlock.create(ctx);
    }

}
