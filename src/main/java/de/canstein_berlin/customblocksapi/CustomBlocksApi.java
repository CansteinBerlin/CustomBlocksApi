package de.canstein_berlin.customblocksapi;

import de.canstein_berlin.customblocksapi.api.ICustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.NamespacedKey;

import java.util.HashMap;

public class CustomBlocksApi implements ICustomBlocksApi {

    private static CustomBlocksApi instance;
    private HashMap<NamespacedKey, CustomBlock> registeredCustomBlocks;

    public CustomBlocksApi() {
        CustomBlocksApi.instance = this;
        registeredCustomBlocks = new HashMap<>();
    }

    public static ICustomBlocksApi getInstance() {
        return CustomBlocksApi.instance != null ? CustomBlocksApi.instance : new CustomBlocksApi();
    }

    @Override
    public boolean register(NamespacedKey key, CustomBlock customBlock, boolean override) {
        customBlock.setKey(key);
        if (override || !registeredCustomBlocks.containsKey(key)) {
            registeredCustomBlocks.put(key, customBlock);
            return true;
        }
        return false;
    }

    @Override
    public CustomBlock getCustomBlock(NamespacedKey key) {
        return registeredCustomBlocks.getOrDefault(key, null);
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
