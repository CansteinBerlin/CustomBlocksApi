package de.canstein_berlin.customblocksapi.api.block.properties;

import org.bukkit.block.BlockFace;

import java.util.Collection;

public class DirectionProperty extends EnumProperty<BlockFace> {

    public DirectionProperty(String name, Collection<BlockFace> values) {
        super(name, BlockFace.class, values);
    }
}
