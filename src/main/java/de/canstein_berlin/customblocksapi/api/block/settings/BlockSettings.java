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
    private String name; // Name of the Block
    private boolean dropsWhenExploded; // Whether the block drops its loot when exploded
    private boolean noBaseBlock;

    protected BlockSettings() { // Empty Block Settings
        baseBlock = Material.AIR;
        displayMaterial = Material.STICK;
        usesNeighborUpdateEvent = false;
        usesEntityMovementEvent = false;
        name = "Custom Block#" + ((int) (Math.random() * 999));
        dropsWhenExploded = false;
        noBaseBlock = false;
    }

    protected BlockSettings(Material baseBlock) {
        this.baseBlock = baseBlock;
    }

    public Material getBaseBlock() {
        return baseBlock;
    }

    protected void setBaseBlock(Material baseBlock) {
        this.baseBlock = baseBlock;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    protected void setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
    }

    public boolean isUsesNeighborUpdateEvent() {
        return usesNeighborUpdateEvent;
    }

    protected void setUsesNeighborUpdateEvent(boolean usesNeighborUpdateEvent) {
        this.usesNeighborUpdateEvent = usesNeighborUpdateEvent;
    }

    public boolean isUsesEntityMovementEvent() {
        return usesEntityMovementEvent;
    }

    protected void setUsesEntityMovementEvent(boolean usesEntityMovementEvent) {
        this.usesEntityMovementEvent = usesEntityMovementEvent;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public boolean isDropsWhenExploded() {
        return dropsWhenExploded;
    }

    protected void setDropsWhenExploded(boolean dropsWhenExploded) {
        this.dropsWhenExploded = dropsWhenExploded;
    }

    public boolean isNoBaseBlock() {
        return noBaseBlock;
    }

    public void setNoBaseBlock(boolean noBaseBlock) {
        this.noBaseBlock = noBaseBlock;
    }
}
