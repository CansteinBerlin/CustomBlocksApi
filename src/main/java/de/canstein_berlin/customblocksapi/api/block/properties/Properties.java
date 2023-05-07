package de.canstein_berlin.customblocksapi.api.block.properties;

import org.bukkit.block.BlockFace;

import java.util.List;

public class Properties {

    public static DirectionProperty FACING;
    public static BooleanProperty POWERED;
    public static BooleanProperty ENABLED;

    static {
        FACING = new DirectionProperty("facing", List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST));
        POWERED = new BooleanProperty("powered");
        ENABLED = new BooleanProperty("enabled");
    }

}
