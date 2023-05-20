package de.canstein_berlin.customblocksapi.api.block.properties;

import org.bukkit.block.BlockFace;

import java.util.List;

/**
 * Basic Class of provided Properties for use in other blocks. It is generally good practice to use these instead of creating you own.
 */
public class Properties {

    public static DirectionProperty FACING; // NORTH, SOUTH, WEST, EAST
    public static BooleanProperty POWERED; // true false
    public static BooleanProperty ENABLED; // true, false
    public static BooleanProperty BERRIES; // true, false

    static {
        FACING = new DirectionProperty("facing", List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST));
        POWERED = new BooleanProperty("powered");
        ENABLED = new BooleanProperty("enabled");
        BERRIES = new BooleanProperty("berries");
    }

}
