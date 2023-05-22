package de.canstein_berlin.customblocksapi.api.block.settings;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.Material;
import org.bukkit.Sound;

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
    public BlockSettingsBuilder baseBlock(Material material) {
        settings.setBaseBlock(material);
        return this;
    }

    /**
     * Change the block's display material
     *
     * @param material Material to set default Material.AIR
     * @return
     */
    public BlockSettingsBuilder displayMaterial(Material material) {
        this.settings.setDisplayMaterial(material);
        return this;
    }

    /**
     * Whether the Neighbor update event is used. default: false
     *
     * @param usesNeighborUpdateEvent
     * @return
     */
    public BlockSettingsBuilder neighborUpdate(boolean usesNeighborUpdateEvent) {
        this.settings.setUsesNeighborUpdateEvent(usesNeighborUpdateEvent);
        return this;
    }

    /**
     * Whether the Stepped On Event is used. default: false
     *
     * @param usesEntityMovement
     * @return
     */
    public BlockSettingsBuilder entityMovement(boolean usesEntityMovement) {
        this.settings.setUsesEntityMovementEvent(usesEntityMovement);
        return this;
    }

    /**
     * Set the custom name of the block
     *
     * @param name
     * @return
     */
    public BlockSettingsBuilder customName(String name) {
        this.settings.setName(name);
        return this;
    }

    /**
     * Set the custom name of the block
     *
     * @param dropsWhenExploded
     * @return
     */
    public BlockSettingsBuilder dropsWhenExploded(boolean dropsWhenExploded) {
        this.settings.setDropsWhenExploded(dropsWhenExploded);
        return this;
    }

    /**
     * Whether the block uses a base block or not
     *
     * @param noBaseBlock
     * @return
     */
    public BlockSettingsBuilder noBaseBlock(boolean noBaseBlock) {
        this.settings.setNoBaseBlock(noBaseBlock);
        return this;
    }

    /**
     * Whether the block is broken instantly. Defaults to false
     *
     * @param breakInstantly
     * @return
     */
    public BlockSettingsBuilder breakInstantly(boolean breakInstantly) {
        this.settings.setBreakInstantly(breakInstantly);
        return this;
    }

    /**
     * The custom place and BreakSounds of the block. These sounds are only played if the placeItem is not a block and the block has no baseBlock
     *
     * @param placeSound The place sound of the block
     * @param breakSound The break Sound of the block
     * @return
     */
    public BlockSettingsBuilder widthSound(Sound placeSound, Sound breakSound) {
        this.settings.setPlaceSound(placeSound);
        this.settings.setBreakSound(breakSound);
        return this;
    }


    /**
     * Set the size in blocks of the custom block
     *
     * @param width
     * @param height
     * @return
     */
    public BlockSettingsBuilder size(float width, float height) {
        this.settings.setSize(width, height);
        return this;
    }

    /**
     * Combine all selected settings to the final BlockSettings
     *
     * @return Combined BlockSettings
     */
    public BlockSettings build() {
        if (settings.isNoBaseBlock()) {
            settings.setUsesEntityMovementEvent(false);
            settings.setBaseBlock(Material.AIR);
        } else {
            size(1f, 1f);
        }
        return settings;
    }

}
