package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;

public class TestBlockHigher extends CustomBlock {

    public TestBlockHigher(BlockSettings settings) {
        super(settings, 4);
    }


}
