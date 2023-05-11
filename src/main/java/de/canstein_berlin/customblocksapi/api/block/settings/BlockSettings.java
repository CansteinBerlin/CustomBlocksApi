package de.canstein_berlin.customblocksapi.api.block.settings;

import org.bukkit.Material;

/**
 * Settings class that holds the settings for a specific block. Should only be created using the Builder
 **/
public class BlockSettings {

    private Material baseBlock; // Base Block that is placed.
    private Material displayMaterial; // Material inside the displayEntity
    private boolean usesNeighborUpdateEvent; // As the BlocksPhysicsEvent is so heavy on the server this has to be set if the onNeighborUpdate event is to be used
    private boolean usesEntityMovementEvent; // As the EntityMovementEvent and PlayerMovementEvent are heavy on the server this has to be set if onSteppedOn is used

    protected BlockSettings() { // Empty Block Settings
        baseBlock = Material.AIR;
        displayMaterial = Material.STICK;
        usesNeighborUpdateEvent = false;
        usesEntityMovementEvent = false;
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

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public void setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
    }

    public boolean isUsesNeighborUpdateEvent() {
        return usesNeighborUpdateEvent;
    }

    public void setUsesNeighborUpdateEvent(boolean usesNeighborUpdateEvent) {
        this.usesNeighborUpdateEvent = usesNeighborUpdateEvent;
    }

    public boolean isUsesEntityMovementEvent() {
        return usesEntityMovementEvent;
    }

    public void setUsesEntityMovementEvent(boolean usesEntityMovementEvent) {
        this.usesEntityMovementEvent = usesEntityMovementEvent;
    }
}
