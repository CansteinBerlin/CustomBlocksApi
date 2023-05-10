package de.canstein_berlin.customblocksapi.api;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public interface ICustomBlocksApi {

    boolean register(NamespacedKey key, CustomBlock customBlock, boolean override);

    boolean register(NamespacedKey key, CustomBlock customBlock);

    CustomBlock getCustomBlock(NamespacedKey key);

    Set<Material> getCustomBlockMaterials();

    boolean usesNeighborUpdate();

    CustomBlockState getStateFromWorld(Location location);

    static NamespacedKey getKeyFromPersistentDataContainer(PersistentDataContainer container) {
        if (!container.has(CustomBlock.CUSTOM_BLOCK_KEY)) return null;
        String stringifiedNameSpacedKey = container.get(CustomBlock.CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        if (stringifiedNameSpacedKey == null) return null;
        return NamespacedKey.fromString(stringifiedNameSpacedKey);
    }

    String getApiName();

}
