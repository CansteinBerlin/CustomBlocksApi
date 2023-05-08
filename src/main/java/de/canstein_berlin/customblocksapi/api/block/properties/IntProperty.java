package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class IntProperty<T> extends Property<Integer> {

    private final ImmutableSet<Integer> values;

    public IntProperty(String name, int min, int max) {
        super(name, Integer.class);

        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        } else if (max <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        } else {
            Set<Integer> set = Sets.newHashSet();

            for (int i = min; i <= max; ++i) {
                set.add(i);
            }

            this.values = ImmutableSet.copyOf(set);
        }
    }

    @Override
    public Collection<Integer> getValues() {
        return values;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return object instanceof IntProperty intProperty && super.equals(object) ? this.values.equals(intProperty.values) : false;
        }
    }

    @Override
    public String name(Integer value) {
        return value.toString();
    }
}
