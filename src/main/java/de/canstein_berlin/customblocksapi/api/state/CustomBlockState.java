package de.canstein_berlin.customblocksapi.api.state;

import de.canstein_berlin.customblocksapi.api.block.properties.Property;

import java.util.HashMap;

public class CustomBlockState {

    private final HashMap<String, Property<?>> properties;

    public CustomBlockState() {
        properties = new HashMap<>();
    }

}
