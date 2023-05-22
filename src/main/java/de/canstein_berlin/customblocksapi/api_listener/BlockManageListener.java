package de.canstein_berlin.customblocksapi.api_listener;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
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
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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

        CustomBlock customBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        if (customBlock == null) return;

        //Cancel Event
        event.setCancelled(true);

        //Get and verify location
        Location placeLocation;
        if (event.getClickedBlock().isReplaceable()) placeLocation = event.getClickedBlock().getLocation();
        else placeLocation = event.getClickedBlock().getLocation().add(event.getBlockFace().getDirection());

        if (!placeLocation.getBlock().isReplaceable()) return; // Only replace replaceable blocks like air or grass
        Collection<Entity> collided = placeLocation.clone().add(
                customBlock.getSettings().getWidth() / 2,
                customBlock.getSettings().getHeight() / 2,
                customBlock.getSettings().getWidth() / 2).getNearbyEntities(
                customBlock.getSettings().getWidth() / 2,
                customBlock.getSettings().getHeight() / 2,
                customBlock.getSettings().getWidth() / 2); // Get entities colliding with the "new Block"
        for (Entity e : collided) { // Ignore Items
            if (!(e instanceof Item)) return;
        }

        //Area Empty?
        if (!checkArea(placeLocation, customBlock.getSettings().getWidth(), customBlock.getSettings().getHeight())) // Area free of blocks
            return;

        clearArea(placeLocation, customBlock.getSettings().getWidth(), customBlock.getSettings().getHeight()); // Clear Area

        //Create ItemPlacement Context
        ItemPlacementContext context = new ItemPlacementContext(event.getPlayer(), event.getHand(), placeLocation, event.getClickedBlock().isReplaceable(), event.getBlockFace());
        //Place Block
        boolean isPlaced = placeBlockInWorld(key, context);

        //Reduce Items
        if (isPlaced) {
            reduceItemStack(event.getPlayer(), event.getHand());
            if (event.getItem().getType().isItem())
                event.getPlayer().getWorld().playSound(placeLocation, customBlock.getSettings().getPlaceSound(), 1, 1);
        }
    }

    @EventHandler
    public void onPlaceBlockOnOther(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return; // Interacting with other custom Blocks
        if (!event.getHand().equals(EquipmentSlot.HAND)) return; //Only use Main Hand.
        if (event.getPlayer().getInventory().getItem(event.getHand()).getType().isAir())
            return; // Check if item not air
        if (!(event.getRightClicked() instanceof Interaction)) return; // Only Interactions

        NamespacedKey itemKey = ICustomBlocksApi.getKeyFromPersistentDataContainer(event.getPlayer().getInventory().getItem(event.getHand()).getItemMeta().getPersistentDataContainer());
        if (itemKey == null) return;

        NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(event.getRightClicked().getPersistentDataContainer());
        if (key == null) return; // Clicked Entity is not a custom Block

        CustomBlock placedBlock = CustomBlocksApi.getInstance().getCustomBlock(key);
        if (placedBlock == null) return;

        CustomBlock toPlaceBlock = CustomBlocksApi.getInstance().getCustomBlock(itemKey);
        if (toPlaceBlock == null) return;

        // Vector now goes from the center of the interaction entity instead of the underside
        Vector direction = event.getClickedPosition().subtract(new Vector(0, placedBlock.getSettings().getHeight() / 2f, 0));
        Vector directionCopy = direction.clone();

        //Reduce to a Vector pointing in the direction of the place Block location
        direction.setY(Math.abs(directionCopy.getX()) == placedBlock.getSettings().getWidth() / 2 || Math.abs(directionCopy.getZ()) == placedBlock.getSettings().getWidth() / 2 ? 0 : directionCopy.getY());
        direction.setX(Math.abs(directionCopy.getY()) == placedBlock.getSettings().getHeight() / 2 || Math.abs(directionCopy.getZ()) == placedBlock.getSettings().getWidth() / 2 ? 0 : directionCopy.getX());
        direction.setZ(Math.abs(directionCopy.getX()) == placedBlock.getSettings().getWidth() / 2 || Math.abs(directionCopy.getY()) == placedBlock.getSettings().getHeight() / 2 ? 0 : directionCopy.getZ());

        direction = direction.normalize();

        //Calculate place Position
        Location clickedPosition = event.getRightClicked().getLocation().add(event.getClickedPosition());
        Location placeLocation = clickedPosition.clone().add(
                new Vector(
                        clamp(direction.getX() * toPlaceBlock.getSettings().getWidth(), -toPlaceBlock.getSettings().getWidth(), Math.ceil(placedBlock.getSettings().getWidth()) - placedBlock.getSettings().getWidth()),
                        clamp(direction.getY() * toPlaceBlock.getSettings().getHeight(), -toPlaceBlock.getSettings().getHeight(), Math.ceil(placedBlock.getSettings().getHeight()) - placedBlock.getSettings().getHeight()),
                        clamp(direction.getZ() * toPlaceBlock.getSettings().getWidth(), -toPlaceBlock.getSettings().getWidth(), Math.ceil(placedBlock.getSettings().getWidth()) - placedBlock.getSettings().getWidth()))
        );
        //placeLocation.setY(Math.ceil(placeLocation.getY()));
        placeLocation = placeLocation.toBlockLocation();

        // Only replace replaceable blocks like air or grass
        if (!placeLocation.getBlock().isReplaceable()) return;
        Collection<Entity> collided = placeLocation.clone().add(
                toPlaceBlock.getSettings().getWidth() / 2,
                toPlaceBlock.getSettings().getHeight() / 2,
                toPlaceBlock.getSettings().getWidth() / 2).getNearbyEntities(
                toPlaceBlock.getSettings().getWidth() / 2,
                toPlaceBlock.getSettings().getHeight() / 2,
                toPlaceBlock.getSettings().getWidth() / 2); // Get entities colliding with the "new Block"
        for (Entity e : collided) { // Ignore Items
            if (!(e instanceof Item)) return;
        }

        //Get BlockFace
        BlockFace placedAgainst;
        if (direction.getX() == -1) placedAgainst = BlockFace.WEST;
        else if (direction.getX() == 1) placedAgainst = BlockFace.EAST;
        else if (direction.getY() == 1) placedAgainst = BlockFace.UP;
        else if (direction.getY() == -1) placedAgainst = BlockFace.DOWN;
        else if (direction.getZ() == 1) placedAgainst = BlockFace.SOUTH;
        else placedAgainst = BlockFace.NORTH;

        //Create Item Context
        ItemPlacementContext context = new ItemPlacementContext(event.getPlayer(), event.getHand(), placeLocation, false, placedAgainst);
        if (!checkArea(placeLocation, toPlaceBlock.getSettings().getWidth(), toPlaceBlock.getSettings().getHeight())) // no Blocks in the way
            return;

        clearArea(placeLocation, toPlaceBlock.getSettings().getWidth(), toPlaceBlock.getSettings().getHeight()); // Blocks to air
        boolean isPlaced = placeBlockInWorld(itemKey, context);

        //Reduce Items
        if (isPlaced) {
            reduceItemStack(event.getPlayer(), event.getHand());
            if (event.getPlayer().getInventory().getItem(event.getHand()).getType().isItem())
                event.getPlayer().getWorld().playSound(placeLocation, toPlaceBlock.getSettings().getPlaceSound(), 1, 1);
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void visualizeLocation(Location loc, Color color) {
        final int[] counter = {0};
        new BukkitRunnable() {

            @Override
            public void run() {
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(color, 1));
                counter[0]++;
                if (counter[0] >= 40) {
                    cancel();
                }
            }
        }.runTaskTimer(CustomBlocksApiPlugin.getInstance(), 0, 1);
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
        boolean stillAlive = state.getParentBlock().onBreak(state, event.getBlock().getWorld(), event.getBlock().getLocation(), event.getPlayer());
        if (!stillAlive) state.remove(event.getBlock().getLocation(), true);
        else event.setCancelled(true);
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
        boolean stillAlive = state.getParentBlock().onBreak(state, event.getEntity().getWorld(), event.getEntity().getLocation().toBlockLocation(), ((Player) event.getDamager()));
        if (!stillAlive) {
            state.remove(event.getEntity().getLocation(), true);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), state.getParentBlock().getSettings().getBreakSound(), 1, 1);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBlockForm(EntityChangeBlockEvent event) {
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;
        event.setCancelled(true);
        event.getEntity().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(event.getTo()));
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getBlock().getLocation());
        if (state == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getToBlock().getLocation());
        if (state == null) return;
        event.setCancelled(true);
    }

    private boolean checkArea(Location start, float width, float height) {
        for (int x = 0; x < Math.ceil(width); x++) {
            for (int y = 0; y < Math.ceil(height); y++) {
                for (int z = 0; z < Math.ceil(width); z++) {
                    if (!start.clone().add(x, y, z).getBlock().isReplaceable()) return false;
                }
            }
        }
        return true;
    }

    private void clearArea(Location start, float width, float height) {
        for (int x = 0; x < Math.ceil(width); x++) {
            for (int y = 0; y < Math.ceil(height); y++) {
                for (int z = 0; z < Math.ceil(width); z++) {
                    start.clone().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlockInstantly(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        CustomBlockState state = CustomBlocksApi.getInstance().getStateFromWorld(event.getClickedBlock().getLocation());
        if (state == null) return;
        boolean stillAlive = state.getParentBlock().onBreak(state, event.getClickedBlock().getWorld(), event.getClickedBlock().getLocation(), event.getPlayer());
        if (!stillAlive) state.remove(event.getClickedBlock().getLocation(), true);
    }


}
