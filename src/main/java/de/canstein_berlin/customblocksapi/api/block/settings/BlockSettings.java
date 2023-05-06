package de.canstein_berlin.customblocksapi.api.block.settings;

import org.bukkit.Material;

/**
 * Settings class that holds the settings for a specific block. Should only be created using the Builder
 **/
public class BlockSettings {

    private Material baseBlock; // Base Block that is placed.

    protected BlockSettings() { // Empty Block Settings
        baseBlock = Material.AIR;
    }

    protected BlockSettings(Material baseBlock) {
        this.baseBlock = baseBlock;
    }

    public Material getBaseBlock() {
        return baseBlock;
    }

    public void setBaseBlock(Material baseBlock) {
        this.baseBlock = baseBlock;
    }
}
