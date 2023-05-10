package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomBlocksApi implements ICustomBlocksApi {

    private static CustomBlocksApi instance;
    private final HashMap<NamespacedKey, CustomBlock> registeredCustomBlocks;
    private final HashSet<Material> customBlockMaterials;
    private boolean usesNeighborUpdate;

    public CustomBlocksApi() {
        CustomBlocksApi.instance = this;
        registeredCustomBlocks = new HashMap<>();
        customBlockMaterials = new HashSet<>();
    }

    public static ICustomBlocksApi getInstance() {
        return CustomBlocksApi.instance != null ? CustomBlocksApi.instance : new CustomBlocksApi();
    }

    @Override
    public boolean register(NamespacedKey key, CustomBlock customBlock, boolean override) {
        customBlock.setKey(key);
        if (override || !registeredCustomBlocks.containsKey(key)) {
            registeredCustomBlocks.put(key, customBlock);
            if (customBlock.getSettings().isUsesNeighborUpdateEvent()) {
                usesNeighborUpdate = true;
                customBlockMaterials.add(customBlock.getSettings().getBaseBlock());
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
    public Set<Material> getCustomBlockMaterials() {
        return customBlockMaterials;
    }

    @Override
    public boolean usesNeighborUpdate() {
        return usesNeighborUpdate;
    }

    @Override
    public CustomBlockState getStateFromWorld(Location location) {
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
    public boolean register(NamespacedKey key, CustomBlock customBlock) {
        return register(key, customBlock, false);
    }

    @Override
    public String getApiName() {
        return "cba";
    }
}
