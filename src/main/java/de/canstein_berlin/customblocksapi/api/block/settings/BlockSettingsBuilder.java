package de.canstein_berlin.customblocksapi.api.block.settings;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Material;

/**
 * Block Settings Builder for easy modification
 */
public class BlockSettingsBuilder {

    private BlockSettings settings;

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
     * @param m Material to set default Material.AIR
     * @return
     */
    public BlockSettingsBuilder withBaseBlock(Material m) {
        settings.setBaseBlock(m);
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
