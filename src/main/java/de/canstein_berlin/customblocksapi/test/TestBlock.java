package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.BooleanProperty;
import de.canstein_berlin.customblocksapi.api.block.properties.Properties;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;

public class TestBlock extends CustomBlock {

    public static BooleanProperty ENABLED = Properties.ENABLED;

    public TestBlock(BlockSettings settings, int customModelData) {
        super(settings, customModelData);
    }
}
