package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;

public class IntProperty<T> extends Property<Integer> {

    int min;
    int max;

    public IntProperty(String name) {
        super(name, Integer.class);
    }

    @Override
    public Collection<Integer> getValues() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = Math.min(min, max); i < Math.max(min, max); i++) {
            values.add(i);
        }
        return new ImmutableList.Builder<Integer>().addAll(values).build();
    }

    @Override
    public String name(Integer value) {
        return String.valueOf(value);
    }
}
