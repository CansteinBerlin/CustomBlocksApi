package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomBlocksApi implements ICustomBlocksApi {

    private static CustomBlocksApi instance;
    private HashMap<NamespacedKey, CustomBlock> registeredCustomBlocks;
    private HashSet<Material> customBlockMaterials;
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
    public boolean register(NamespacedKey key, CustomBlock customBlock) {
        return register(key, customBlock, false);
    }

    @Override
    public String getApiName() {
        return "cba";
    }
}
