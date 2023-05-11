package de.canstein_berlin.customblocksapi.api.block.properties;

import org.bukkit.block.BlockFace;

import java.util.Collection;

/**
 * Direction Property mostly used for blocks that want to be rotated in different directions
 */
public class DirectionProperty extends EnumProperty<BlockFace> {

    public DirectionProperty(String name, Collection<BlockFace> values) {
        super(name, BlockFace.class, values);
    }
}
