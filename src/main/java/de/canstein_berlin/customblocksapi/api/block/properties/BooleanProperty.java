package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getValues() {
        return new ImmutableList.Builder<Boolean>().add(false, true).build();

    }

    @Override
    public String name(Boolean value) {
        return value.toString();
    }

}
