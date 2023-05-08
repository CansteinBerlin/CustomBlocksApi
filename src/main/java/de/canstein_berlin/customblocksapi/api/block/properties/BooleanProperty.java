package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class BooleanProperty extends Property<Boolean> {

    private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

    public BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getValues() {
        return values;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return object instanceof BooleanProperty booleanProperty && super.equals(object) && this.values.equals(booleanProperty.values);
        }
    }

    @Override
    public String name(Boolean value) {
        return value.toString();
    }

}
