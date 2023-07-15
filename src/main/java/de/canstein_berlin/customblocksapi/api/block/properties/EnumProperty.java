package de.canstein_berlin.customblocksapi.api.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * General Enum Property for saving custom Enums to the blockstates
 *
 * @param <T>
 */
public class EnumProperty<T extends Enum<T>> extends Property<T> {

    private ImmutableSet<T> values;
    private Map<String, T> byName = Maps.newHashMap();

    public EnumProperty(String name, Class<T> type, Collection<T> values) {
        super(name, type);
        this.values = ImmutableSet.copyOf(values);

        for (T value : values) {
            String _name = name(value);
            if (byName.containsKey(_name)) {
                throw new IllegalArgumentException("Multiple value has the same name '" + _name + "'");
            }
            byName.put(_name, value);
        }
    }

    public EnumProperty(String name, Class<T> type) {
        this(name, type, List.of(type.getEnumConstants()));
    }

    @Override
    public Collection<T> getValues() {
        return values;
    }

    @Override
    public String name(T value) {
        return value.name();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof EnumProperty enumProperty && super.equals(object)) {
            return this.values.equals(enumProperty.values) && this.byName.equals(enumProperty.byName);
        } else {
            return false;
        }
    }

    @Override
    public Value<T> parse(String value) {
        if (byName.containsKey(value)) return createValue(byName.get(value));
        return null;
    }

}
