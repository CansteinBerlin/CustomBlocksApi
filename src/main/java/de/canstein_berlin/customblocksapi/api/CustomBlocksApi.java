package de.canstein_berlin.customblocksapi.api;

import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import de.canstein_berlin.customblocksapi.api.tick.ITickable;
import de.canstein_berlin.customblocksapi.api.tick.TickState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class CustomBlocksApi implements ICustomBlocksApi {

    private static CustomBlocksApi instance;
    private final HashMap<NamespacedKey, CustomBlock> registeredCustomBlocks;
    private final HashSet<Material> neighborUpdateBlockMaterials;
    private final HashSet<Material> entityMovementBlockMaterials;
    private final HashSet<Material> customBlockMaterials;
    private boolean usesNeighborUpdate, usesEntityMovement;
    private final HashMap<ITickable, HashMap<UUID, TickState>> tickableStates;

    private CustomBlocksApi() {
        CustomBlocksApi.instance = this;
        registeredCustomBlocks = new HashMap<>();
        neighborUpdateBlockMaterials = new HashSet<>();
        customBlockMaterials = new HashSet<>();
        entityMovementBlockMaterials = new HashSet<>();
        usesNeighborUpdate = false;
        usesEntityMovement = false;
        tickableStates = new HashMap<>();
    }

    public static ICustomBlocksApi getInstance() {
        return CustomBlocksApi.instance != null ? CustomBlocksApi.instance : new CustomBlocksApi();
    }

    @Override
    public boolean register(NamespacedKey key, CustomBlock customBlock, boolean override) {
        customBlock.setKey(key);
        if (override || !registeredCustomBlocks.containsKey(key)) {
            registeredCustomBlocks.put(key, customBlock);
            customBlockMaterials.add(customBlock.getSettings().getBaseBlock());

            //Register for updates
            if (customBlock.getSettings().isUsesNeighborUpdateEvent()) {
                usesNeighborUpdate = true;
                neighborUpdateBlockMaterials.add(customBlock.getSettings().getBaseBlock());
            }

            if (customBlock.getSettings().isUsesEntityMovementEvent()) {
                usesEntityMovement = true;
                entityMovementBlockMaterials.add(customBlock.getSettings().getBaseBlock());
            }

            //Register for Ticks
            if (customBlock instanceof ITickable) {
                tickableStates.put((ITickable) customBlock, new HashMap<>());
                registerBlockTicker(customBlock);
            }
            return true;
        }
        return false;
    }

    private void registerBlockTicker(CustomBlock customBlock) {
        if (!(customBlock instanceof ITickable)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, TickState> tickStates : tickableStates.get(customBlock).entrySet()) {
                    tickStates.getValue().incrementCount();
                    ((ITickable) customBlock).onTick(tickStates.getValue());
                }
            }
        }.runTaskTimer(CustomBlocksApiPlugin.getInstance(), 0, ((ITickable) customBlock).getTickDelay());
    }

    @Override
    public CustomBlock getCustomBlock(NamespacedKey key) {
        return registeredCustomBlocks.getOrDefault(key, null);
    }

    @Override
    public Set<Material> getNeighborUpdateBlockMaterials() {
        return neighborUpdateBlockMaterials;
    }

    @Override
    public Set<Material> getEntityMovementBlockMaterials() {
        return entityMovementBlockMaterials;
    }

    @Override
    public boolean usesNeighborUpdate() {
        return usesNeighborUpdate;
    }

    @Override
    public boolean usesEntityMovement() {
        return usesEntityMovement;
    }

    @Override
    public void setBlockStateToTick(CustomBlock customBlock, CustomBlockState state) {
        if (!(customBlock instanceof ITickable)) return;
        if (state.getDisplay() == null) return;
        if (!tickableStates.containsKey(customBlock))
            throw new RuntimeException("Tried to add a non tickable block to the ticked block list");

        //No BlockState Found
        if (!tickableStates.get(customBlock).containsKey(state.getDisplay())) {
            tickableStates.get(customBlock).put(state.getDisplay().getUniqueId(), new TickState(state, 0));
            return;
        }

        //Replace BlockState
        tickableStates.get(customBlock).get(state.getDisplay().getUniqueId()).update(state);

    }

    @Override
    public TickState getTickedBlockFromEntity(CustomBlock customBlock, ItemDisplay display) {
        if (!(customBlock instanceof ITickable)) return null;
        if (tickableStates.get(customBlock) == null) return null;
        return tickableStates.get(customBlock).get(display);
    }

    @Override
    public void removeTickedBlock(CustomBlock block, CustomBlockState state) {
        if (!(block instanceof ITickable)) return;
        if (!tickableStates.containsKey(block)) return;

        tickableStates.get(block).remove(state.getDisplay().getUniqueId());
    }

    @Override
    public List<CustomBlock> getAllCustomBlocks() {
        return List.copyOf(registeredCustomBlocks.values());
    }

    @Override
    public CustomBlockState getStateFromWorld(Location location) {
        //Display
        BoundingBox box = BoundingBox.of(location.toBlockLocation().add(0.5, 0.5, 0.5), 0.05, 0.05, 0.05);
        Collection<Entity> displays = location.getWorld().getNearbyEntities(box, (entity) -> entity instanceof ItemDisplay);

        ItemDisplay foundDisplay = null;
        CustomBlock foundBlock = null;
        for (Entity e : displays) {
            if (!(e instanceof ItemDisplay display)) continue;

            //Check Block
            NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(display.getPersistentDataContainer());
            if (key == null) continue;
            CustomBlock block = getCustomBlock(key);
            if (block == null) continue;

            foundDisplay = display;
            foundBlock = block;
        }

        //Found Display, now check if it is tickable and cached
        if (foundBlock instanceof ITickable) {
            TickState state = getTickedBlockFromEntity(foundBlock, foundDisplay);
            if (state != null) return state.getCustomBlockState();
        }

        //Found Display and block has base block
        if (foundBlock != null && !foundBlock.getSettings().isNoBaseBlock())
            return new CustomBlockState(foundBlock, foundDisplay, null);

        //Display Might be null so let's check for the interaction entity
        Collection<Entity> interactions = location.getWorld().getNearbyEntities(box, (entity) -> entity instanceof Interaction);
        if (interactions.size() == 0 && foundDisplay == null) return null;
        Interaction foundInteraction = null;
        for (Entity e : interactions) {
            if (!(e instanceof Interaction interaction)) continue;
            NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(interaction.getPersistentDataContainer());
            if (key == null) continue;
            CustomBlock block = getCustomBlock(key);
            if (block == null) continue;
            if (foundBlock != null && !foundBlock.getKey().equals(key)) // Found both interaction and display, but different blocks
                continue;
            foundInteraction = interaction;
            foundBlock = block;
        }

        //Found no interaction and no display
        if (foundBlock == null) return null;

        //Found no Display but interaction
        if (foundDisplay == null) return getStateFromWorld(foundInteraction);

        return new CustomBlockState(foundBlock, foundDisplay, foundInteraction);
    }


    @Override
    public CustomBlockState getStateFromWorld(Entity e) {
        if (e == null) return null;
        NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(e.getPersistentDataContainer());
        if (key == null) return null;

        CustomBlock block = getCustomBlock(key);
        if (block == null) return null;

        //Entity may either be an ItemDisplay or interaction
        ItemDisplay display = e instanceof ItemDisplay ? ((ItemDisplay) e) : null;
        Interaction interaction = e instanceof Interaction ? ((Interaction) e) : null;
        if (display == null) { //The entity was an interaction, so we have to find the corresponding display
            Collection<ItemDisplay> displays = e.getLocation().add(0, 0.5, 0).getNearbyEntitiesByType(ItemDisplay.class, 0.1);
            for (ItemDisplay d : displays) {
                NamespacedKey displayKey = ICustomBlocksApi.getKeyFromPersistentDataContainer(d.getPersistentDataContainer());
                if (key.equals(displayKey)) {
                    display = d;
                    break;
                }
            }
        }
        if (display == null) return null; // Invalid Block

        //Check if display is in Ticked block list
        if (block instanceof ITickable) {
            TickState state = CustomBlocksApi.getInstance().getTickedBlockFromEntity(block, display);
            if (state != null) return state.getCustomBlockState();
        }

        if (interaction == null && block.getSettings().isNoBaseBlock()) {
            Collection<Interaction> interactions = e.getLocation().toBlockLocation().add(0, 0.5, 0).getNearbyEntitiesByType(Interaction.class, 0.1);
            for (Interaction i : interactions) {
                NamespacedKey interactionKey = ICustomBlocksApi.getKeyFromPersistentDataContainer(i.getPersistentDataContainer());
                if (key.equals(interactionKey)) {
                    interaction = i;
                    break;
                }
            }

            if (interaction == null) return null; // Invalid Block
        }


        CustomBlockState newState = new CustomBlockState(block, display, interaction);
        if (block instanceof ITickable) { //If BlockState is tickable save/update it
            setBlockStateToTick(block, newState);
        }

        return newState;
    }

    @Override
    public void setBlock(Location location, CustomBlock customBlock) {
        boolean replaces = !location.getBlock().getType().isAir();
        CustomBlockState state = getStateFromWorld(location);
        if (state != null) {
            state.remove(location, false);
            replaces = true;
        }

        customBlock.create(new ItemPlacementContext(null, EquipmentSlot.HAND, location, replaces, BlockFace.NORTH));
    }

    @Override
    public boolean register(NamespacedKey key, CustomBlock customBlock) {
        return register(key, customBlock, false);
    }

    @Override
    public String getApiName() {
        return "cba";
    }

    public int cachedBlocks() {
        int count = 0;
        for (ITickable tickable : tickableStates.keySet()) {
            count += tickableStates.get(tickable).size();
        }
        return count;
    }
}
