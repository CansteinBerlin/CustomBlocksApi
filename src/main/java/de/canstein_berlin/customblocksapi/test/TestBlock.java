package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.*;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import org.bukkit.block.BlockFace;

public class TestBlock extends CustomBlock {

    public static BooleanProperty ENABLED;
    public static EnumProperty<BlockFace> FACING;
    public static IntProperty AGE0_5;

    static {
        ENABLED = Properties.ENABLED;
        FACING = Properties.FACING;
        AGE0_5 = new IntProperty("age", 0, 5);

    }

    public TestBlock(BlockSettings settings, int customModelData) {
        super(settings, customModelData);
    }


    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(ENABLED, FACING, AGE0_5);
    }
}
