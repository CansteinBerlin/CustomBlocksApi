package de.canstein_berlin.customblocksapi.api.block.properties;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;

import java.util.List;

/**
 * Basic Class of provided Properties for use in other blocks. It is generally good practice to use these instead of creating you own.
 */
public class Properties {

    public static DirectionProperty FACING; // NORTH, SOUTH, WEST, EAST
    public static DirectionProperty DIRECTION; // NORTH, SOUTH, WEST, EAST, UP, DOWN
    public static BooleanProperty POWERED; // true false
    public static BooleanProperty ENABLED; // true, false
    public static BooleanProperty BERRIES; // true, false
    public static EnumProperty<Axis> AXIS; // x, y, z
    public static EnumProperty<Axis> HORIZONTAL_AXIS; //x, z

    static {
        FACING = new DirectionProperty("facing", List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST));
        DIRECTION = new DirectionProperty("direction", List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.UP, BlockFace.DOWN));
        POWERED = new BooleanProperty("powered");
        ENABLED = new BooleanProperty("enabled");
        BERRIES = new BooleanProperty("berries");
        AXIS = new EnumProperty<>("axis", Axis.class, List.of(Axis.X, Axis.Y, Axis.Z));
        HORIZONTAL_AXIS = new EnumProperty<>("horizontal_axis", Axis.class, List.of(Axis.X, Axis.Z));
    }

}
