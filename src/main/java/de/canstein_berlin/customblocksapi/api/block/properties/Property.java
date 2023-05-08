package de.canstein_berlin.customblocksapi.api.block.properties;

import java.util.Collection;

public abstract class Property<T extends Comparable<T>> {

    private final String name;
    private final Class<T> type;

    public Property(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public abstract Collection<T> getValues();

    public abstract String name(T value);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Property)) {
            return false;
        } else {
            Property<?> property = (Property) obj;
            return type.equals(property.type) && this.name.equals(property.name);
        }
    }

    public Property.Value<T> createValue(T value) {
        return new Property.Value<>(this, value);
    }

    public record Value<T extends Comparable<T>>(Property<T> property, T value) {
        public Value(Property<T> property, T value) {
            if (!property.getValues().contains(value)) {
                throw new IllegalArgumentException("Value " + value + " does not belong to property " + property);
            } else {
                this.property = property;
                this.value = value;
            }
        }

        public String toString() {
            return this.property.getName() + "=" + this.property.name(this.value);
        }
    }

}
