package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.context.ItemPlacementContext;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomBlocksApi implements ICustomBlocksApi {

    private static CustomBlocksApi instance;
    private final HashMap<NamespacedKey, CustomBlock> registeredCustomBlocks;
    private final HashSet<Material> neighborUpdateBlockMaterials;
    private final HashSet<Material> entityMovementBlockMaterials;
    private final HashSet<Material> customBlockMaterials;
    private boolean usesNeighborUpdate, usesEntityMovement;

    public CustomBlocksApi() {
        CustomBlocksApi.instance = this;
        registeredCustomBlocks = new HashMap<>();
        neighborUpdateBlockMaterials = new HashSet<>();
        customBlockMaterials = new HashSet<>();
        entityMovementBlockMaterials = new HashSet<>();
        usesNeighborUpdate = false;
        usesEntityMovement = false;
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
            if (customBlock.getSettings().isUsesNeighborUpdateEvent()) {
                usesNeighborUpdate = true;
                neighborUpdateBlockMaterials.add(customBlock.getSettings().getBaseBlock());
            }

            if (customBlock.getSettings().isUsesEntityMovementEvent()) {
                usesEntityMovement = true;
                entityMovementBlockMaterials.add(customBlock.getSettings().getBaseBlock());
            }
            return true;
        }
        return false;
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
    public CustomBlockState getStateFromWorld(Location location) {
        if (!customBlockMaterials.contains(location.getBlock().getType())) return null;
        Collection<ItemDisplay> displays = location.toBlockLocation().add(0.5, 0.5, 0.5).getNearbyEntitiesByType(ItemDisplay.class, 0.1);
        if (displays.size() == 0) return null;
        CustomBlock block = null;
        ItemDisplay display = null;
        for (ItemDisplay d : displays) {
            if (d.getPersistentDataContainer().has(CustomBlock.CUSTOM_BLOCK_KEY)) {
                NamespacedKey key = ICustomBlocksApi.getKeyFromPersistentDataContainer(d.getPersistentDataContainer());
                if (key == null) continue;
                block = getCustomBlock(key);
                if (block != null) {
                    display = d;
                    break;
                }
            }
        }
        if (block == null) return null;
        return new CustomBlockState(block, display);
    }

    @Override
    public void setBlock(Location location, CustomBlock customBlock) {
        boolean replaces = !location.getBlock().getType().isAir();
        CustomBlockState state = getStateFromWorld(location);
        if (state != null) {
            state.remove(location);
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
}
