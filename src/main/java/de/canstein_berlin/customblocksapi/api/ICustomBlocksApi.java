package de.canstein_berlin.customblocksapi.api;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.NamespacedKey;

public interface ICustomBlocksApi {

    boolean register(NamespacedKey key, CustomBlock customBlock, boolean override);

    boolean register(NamespacedKey key, CustomBlock customBlock);

    CustomBlock getCustomBlock(NamespacedKey key);

    String getApiName();

}
