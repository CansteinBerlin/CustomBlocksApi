package de.canstein_berlin.customblocksapi.api.block.properties;

import java.util.Collection;

public abstract class Property<T extends Comparable<T>> {

    private String name;
    private Class<T> type;

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


    public static class Value<T extends Comparable<T>> {
        private final Property<T> property;
        private final T value;

        public Value(Property<T> property, T value) {
            this.property = property;
            this.value = value;
        }

        public Property<T> getProperty() {
            return property;
        }

        public T getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            return value.equals(obj);
        }
    }

}
