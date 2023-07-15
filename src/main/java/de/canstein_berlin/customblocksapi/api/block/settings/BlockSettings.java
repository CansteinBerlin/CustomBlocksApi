package de.canstein_berlin.customblocksapi.api.block.settings;

import org.bukkit.Material;
import org.bukkit.Sound;

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
    private boolean noBaseBlock; // Whether the block has no base block
    private float width; // Width of the default block defaults to 1
    private float height; // Height of the default block defaults to 1
    private Sound placeSound;
    private Sound breakSound;
    private boolean breakInstantly;


    protected BlockSettings() { // Empty Block Settings
        baseBlock = Material.STONE;
        displayMaterial = Material.STICK;
        usesNeighborUpdateEvent = false;
        usesEntityMovementEvent = false;
        name = "Custom Block#" + ((int) (Math.random() * 99999));
        dropsWhenExploded = true;
        noBaseBlock = false;
        width = 1;
        height = 1;
        placeSound = Sound.BLOCK_STONE_PLACE;
        breakSound = Sound.BLOCK_STONE_BREAK;
        breakInstantly = false;
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

    public void setSize(float width, float height) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Attempted to create a block with negative with or height");
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Sound getPlaceSound() {
        return placeSound;
    }

    public void setPlaceSound(Sound placeSound) {
        this.placeSound = placeSound;
    }

    public Sound getBreakSound() {
        return breakSound;
    }

    public void setBreakSound(Sound breakSound) {
        this.breakSound = breakSound;
    }

    public void setBreakInstantly(boolean breakInstantly) {
        this.breakInstantly = breakInstantly;
    }

    public boolean isBreakInstantly() {
        return breakInstantly;
    }

    public BlockSettings clone() {
        BlockSettings settings = new BlockSettings();
        settings.setBaseBlock(baseBlock);
        settings.setBreakInstantly(breakInstantly);
        settings.setName(name);
        settings.setBreakSound(breakSound);
        settings.setPlaceSound(placeSound);
        settings.setSize(width, height);
        settings.setUsesEntityMovementEvent(usesEntityMovementEvent);
        settings.setUsesNeighborUpdateEvent(usesNeighborUpdateEvent);
        settings.setDisplayMaterial(displayMaterial);
        settings.setDropsWhenExploded(dropsWhenExploded);
        settings.setNoBaseBlock(noBaseBlock);
        return settings;
    }
}
