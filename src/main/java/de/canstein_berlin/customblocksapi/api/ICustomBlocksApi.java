package de.canstein_berlin.customblocksapi.api;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Set;

public interface ICustomBlocksApi {

    boolean register(NamespacedKey key, CustomBlock customBlock, boolean override);

    boolean register(NamespacedKey key, CustomBlock customBlock);

    CustomBlock getCustomBlock(NamespacedKey key);

    Set<Material> getCustomBlockMaterials();

    boolean usesNeighborUpdate();

    String getApiName();

}
