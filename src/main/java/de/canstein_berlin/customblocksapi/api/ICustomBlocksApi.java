package de.canstein_berlin.customblocksapi.api;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public interface ICustomBlocksApi {

    /**
     * Registers a block under a given NamespacedKey if a block with the same key exists and override is true the block will be overridden
     *
     * @param key         The key the block is registered under
     * @param customBlock The CustomBlockObject that is registered
     * @param override    Whether to override an existing block or not
     * @return Whether the registration was successful
     */
    boolean register(NamespacedKey key, CustomBlock customBlock, boolean override);

    /**
     * Registers a block under a given NamespacesKey, not overriding other blocks
     *
     * @param key         The key the block is registered under
     * @param customBlock The CustomBlocKObject that is registered
     * @return Whether the registration was successful
     */
    boolean register(NamespacedKey key, CustomBlock customBlock);

    /**
     * Return the Custom Block object of a NamespacedKey or null if none exist.
     *
     * @param key The Block key the customBlock is registered under
     * @return Valid CustomBlock object or null if not registered
     */
    CustomBlock getCustomBlock(NamespacedKey key);

    /**
     * Get a CustomBlockState placed in the world at a specific location, or null if the block at the location is not a custom block
     *
     * @param location The Location to get the block from
     * @return CustomBlockState or null if block at location is not a custom block
     */
    CustomBlockState getStateFromWorld(Location location);

    /**
     * Get a CustomBlockState from an entity, or null if the entity is not a custom block
     *
     * @param e The Entity that may be a custom block
     * @return CustomBlockState or null if the entity is not a custom block
     */
    CustomBlockState getStateFromWorld(Entity e);

    /**
     * Set a custom block in the world
     *
     * @param location    The location to place the block at
     * @param customBlock The Custom Block to place
     */
    void setBlock(Location location, CustomBlock customBlock);

    /**
     * Util method for usage inside the plugin.
     *
     * @return A List of registered base Block materials of all blocks that want to use the neighbor update event
     */
    Set<Material> getNeighborUpdateBlockMaterials();

    /**
     * Util method for usage inside the plugin.
     *
     * @return A List of registered base Block materials of all blocks that want to use the entity movement event
     */
    Set<Material> getEntityMovementBlockMaterials();

    /**
     * Util method for usage inside the plugin.
     *
     * @return Whether there is a block that wants to use the neighbor update event
     */
    boolean usesNeighborUpdate();

    /**
     * Util method for usage inside the plugin.
     *
     * @return Whether there is a block that wants to use the entityMovement event
     */
    boolean usesEntityMovement();

    static NamespacedKey getKeyFromPersistentDataContainer(PersistentDataContainer container) {
        if (!container.has(CustomBlock.CUSTOM_BLOCK_KEY)) return null;
        String stringifiedNameSpacedKey = container.get(CustomBlock.CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        if (stringifiedNameSpacedKey == null) return null;
        return NamespacedKey.fromString(stringifiedNameSpacedKey);
    }

    /**
     * Provides the API Name used for data storage aso.
     *
     * @return
     */
    String getApiName();

}
