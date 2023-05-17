package de.canstein_berlin.customblocksapi.listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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
     * Event to listen to the placement of custom blocks on solid blocks
     *
     * @param event
     */
    @EventHandler
    public void onItemUse(PlayerInteractEvent event) { // Items placed on solid blocks
        if (event.isCancelled()) return; // Interacting with other custom Blocks
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return; // Ignore Left Click
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getItem() == null) return; // Check if item not air
        if (event.getInteractionPoint() == null) return; // Check if clicked on Block
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().isInteractable() && !event.getPlayer().isSneaking())
            return; // Semi normal Block Entity behaviour

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
        boolean isPlaced = placeBlockInWorld(key, context);

        //Reduce Items
        if (isPlaced) reduceItemStack(event.getPlayer(), event.getHand());
    }

    @EventHandler
    public void onPlaceBlockOnOther(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return; // Interacting with other custom Blocks
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getPlayer().getInventory().getItem(event.getHand()).getType().isAir())
            return; // Check if item not air
        if (!(event.getRightClicked() instanceof Interaction)) return; // Only Interactions

        NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(event.getRightClicked().getPersistentDataContainer());
        if (key == null) return; // Clicked Entity is not a custom Block

        Vector direction = event.getClickedPosition().subtract(new Vector(0, 0.5, 0)); // Vector now goes from the center of the block instead of the underside
        //Reduce to a Vector pointing in the direction of the place Block location
        direction.setY(Math.abs(direction.getX()) == 0.5 || Math.abs(direction.getZ()) == 0.5 ? 0 : direction.getY());
        direction.setX(Math.abs(direction.getY()) == 0.5 || Math.abs(direction.getZ()) == 0.5 ? 0 : direction.getX());
        direction.setZ(Math.abs(direction.getX()) == 0.5 || Math.abs(direction.getY()) == 0.5 ? 0 : direction.getZ());

        //Location the block is placed at
        Location blockPlace = event.getRightClicked().getLocation().toBlockLocation().add(0.5, 0.5, 0.5).add(direction.clone().multiply(1.01)).toBlockLocation();

        if (!blockPlace.getBlock().isReplaceable()) return; // Only replace replaceable blocks like air or grass
        Collection<Entity> collided = blockPlace.clone().add(0.5, 0.5, 0.5).getNearbyEntities(0.5, 0.5, 0.5); // Get entities colliding with the "new Block"
        for (Entity e : collided) { // Ignore Items
            if (!(e instanceof Item)) return;
        }

        //Get BlockFace
        BlockFace placedAgainst;
        if (direction.getX() == -0.5) placedAgainst = BlockFace.WEST;
        else if (direction.getX() == 0.5) placedAgainst = BlockFace.EAST;
        else if (direction.getY() == 0.5) placedAgainst = BlockFace.UP;
        else if (direction.getY() == -0.5) placedAgainst = BlockFace.DOWN;
        else if (direction.getZ() == 0.5) placedAgainst = BlockFace.SOUTH;
        else placedAgainst = BlockFace.NORTH;

        //Create Item Context
        ItemPlacementContext context = new ItemPlacementContext(event.getPlayer(), event.getHand(), blockPlace, false, placedAgainst);
        boolean isPlaced = placeBlockInWorld(key, context);

        //Reduce Items
        if (isPlaced) reduceItemStack(event.getPlayer(), event.getHand());
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
        state.remove(event.getBlock().getLocation(), true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;

            state.getParentBlock().onDestroyedByExplosion(state, event.getBlock().getWorld(), block.getLocation());
            state.remove(block.getLocation(), state.getParentBlock().getSettings().isDropsWhenExploded());
        }

        removeBlocksInExplosionRange(event.getBlock().getWorld(), BoundingBox.of(event.getBlock().getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5), 5f);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(block.getLocation());
            if (state == null) continue;

            state.getParentBlock().onDestroyedByExplosion(state, block.getWorld(), block.getLocation());
            state.remove(block.getLocation(), state.getParentBlock().getSettings().isDropsWhenExploded());
        }

        if (!(event.getEntity() instanceof Explosive)) return;
        Explosive explosive = ((Explosive) event.getEntity());
        removeBlocksInExplosionRange(event.getEntity().getWorld(), event.getEntity().getBoundingBox(), explosive.getYield());
    }

    private void removeBlocksInExplosionRange(World world, BoundingBox box, float radius) {
        Collection<Entity> interactions = world.getNearbyEntities(box.expand(radius), e -> e instanceof Interaction);
        for (Entity e : interactions) {
            if (!(e instanceof Interaction)) continue;

            CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(e);
            if (state == null) continue;

            state.getParentBlock().onDestroyedByExplosion(state, e.getWorld(), e.getLocation().toBlockLocation());
            state.remove(e.getLocation().toBlockLocation(), state.getParentBlock().getSettings().isDropsWhenExploded());
        }
    }

    private void reduceItemStack(Player player, EquipmentSlot slot) {
        //Infinite items if player is in creative or spectator
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) // Infinite Items in GameMode
            return;

        //Reduce Item stack
        ItemStack stack = player.getInventory().getItem(slot);
        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() <= 0)
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));
    }

    private boolean placeBlockInWorld(NamespacedKey key, ItemPlacementContext ctx) {
        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        return customBlock.create(ctx);
    }

    //No Base block entities
    @EventHandler
    public void onPlayerAttackInteraction(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return; // Player in the attacker
        if (!(event.getEntity() instanceof Interaction)) return; // Attack Interaction entity

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getEntity().getLocation());
        if (state == null) return;
        state.getParentBlock().onBreak(state, event.getEntity().getWorld(), event.getEntity().getLocation().toBlockLocation(), ((Player) event.getDamager()));
        state.remove(event.getEntity().getLocation(), true);
        event.setCancelled(true);
    }


}
