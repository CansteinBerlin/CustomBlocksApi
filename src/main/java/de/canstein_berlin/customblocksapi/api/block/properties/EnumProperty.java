package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnumProperty<T extends Enum<T>> extends Property<T> {

    private ImmutableSet<T> values;
    private Map<String, T> byName;

    public EnumProperty(String name, Class<T> type, Collection<T> values) {
        super(name, type);
        this.values = new ImmutableSet.Builder<T>().addAll(values).build();

        byName = new HashMap<>();
        for (T value : values) {
            byName.put(name(value), value);
        }
    }

    @Override
    public Collection<T> getValues() {
        return values;
    }

    @Override
    public String name(T value) {
        return value.name();
    }

}
