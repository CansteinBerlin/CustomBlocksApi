package de.canstein_berlin.customblocksapi.api.block.properties;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;

import java.util.HashMap;

public class PropertyListBuilder {

    private HashMap<String, Property<?>> properties;
    private CustomBlock customBlock;

    public PropertyListBuilder(CustomBlock customBlock) {
        this.customBlock = customBlock;
        properties = new HashMap<>();

    }

    public PropertyListBuilder add(Property<?>... passed) {
        for (Property<?> property : passed) {
            if (validate(property)) {
                this.properties.put(property.getName(), property);
            }
        }
        return this;
    }

    private boolean validate(Property<?> property) {
        if (this.properties.containsKey(property.getName())) {
            throw new IllegalArgumentException("Attempted to register duplicate property with name " + property.getName() + " on block \"" + customBlock.getSettings().getName() + "\"");
        }
        return true;
    }

    public HashMap<String, Property<?>> build() {
        return properties;
    }

}
