package de.canstein_berlin.customblocksapi.api.block.settings;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Material;

/**
 * Block Settings Builder for easy modification
 */
public class BlockSettingsBuilder {

    private final BlockSettings settings;

    private BlockSettingsBuilder() {
        settings = new BlockSettings();
    }

    private BlockSettingsBuilder(CustomBlock customBlock) {
        this.settings = customBlock.getSettings();
    }

    /**
     * Creates a BlockSettingsBuilder based of a Custom Block's settings
     *
     * @param customBlock Custom Block whose settings to copy
     * @return BlockSettingsBuilder with copied settings
     */
    public static BlockSettingsBuilder of(CustomBlock customBlock) {
        return new BlockSettingsBuilder(customBlock);
    }

    /**
     * Creates an empty BlockSettingsBuilder
     *
     * @return Empty BlockSettingsBuilder
     */
    public static BlockSettingsBuilder empty() {
        return new BlockSettingsBuilder();
    }

    /**
     * Change the base block's material of the block
     *
     * @param material Material to set default Material.AIR
     * @return
     */
    public BlockSettingsBuilder withBaseBlock(Material material) {
        settings.setBaseBlock(material);
        return this;
    }

    /**
     * Change the block's display material
     *
     * @param material Material to set default Material.AIR
     * @return
     */
    public BlockSettingsBuilder withDisplayMaterial(Material material) {
        this.settings.setDisplayMaterial(material);
        return this;
    }

    /**
     * Whether the Neighbor update event is used. default: false
     *
     * @param usesNeighborUpdateEvent
     * @return
     */
    public BlockSettingsBuilder withNeighborUpdate(boolean usesNeighborUpdateEvent) {
        this.settings.setUsesNeighborUpdateEvent(usesNeighborUpdateEvent);
        return this;
    }

    /**
     * Whether the Stepped On Event is used. default: false
     *
     * @param usesEntityMovement
     * @return
     */
    public BlockSettingsBuilder withEntityMovement(boolean usesEntityMovement) {
        this.settings.setUsesEntityMovementEvent(usesEntityMovement);
        return this;
    }

    /**
     * Combine all selected settings to the final BlockSettings
     *
     * @return Combined BlockSettings
     */
    public BlockSettings build() {
        return settings;
    }

}
