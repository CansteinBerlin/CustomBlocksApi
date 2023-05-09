package de.canstein_berlin.customblocksapi.api.state;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomBlockState {

    public static final NamespacedKey PROPERTIES_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "properties");
    public static final NamespacedKey NAME_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "name");
    public static final NamespacedKey VALUE_KEY = new NamespacedKey(CustomBlocksApi.getInstance().getApiName(), "value");


    private final CustomBlock parentBlock;
    @Nullable
    private ItemDisplay display;
    private final HashMap<Property<?>, Property.Value<?>> propertyValues;

    public CustomBlockState(CustomBlock parentBlock, ImmutableMap<String, Property<?>> properties) {
        this.parentBlock = parentBlock;

        propertyValues = new HashMap<>();
        for (Map.Entry<String, Property<?>> element : properties.entrySet()) {
            Property<?> property = element.getValue();
            propertyValues.put(property, property.createDefaultValue());
        }
    }

    public CustomBlockState(CustomBlock parentBlock, ItemDisplay display) {
        this.parentBlock = parentBlock;

        propertyValues = new HashMap<>();
        PersistentDataContainer dataContainer = display.getPersistentDataContainer();
        PersistentDataContainer[] elements = dataContainer.get(PROPERTIES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (elements == null) {
            return;
        }
        for (PersistentDataContainer container : elements) {
            String name = container.get(NAME_KEY, PersistentDataType.STRING);
            if (name == null)
                throw new IllegalArgumentException("Malformed property data  on block " + parentBlock.getKey());
            String value = container.get(VALUE_KEY, PersistentDataType.STRING);
            if (value == null)
                throw new IllegalArgumentException("Malformed property data  on block " + parentBlock.getKey());

            Property<?> property = parentBlock.getProperties().get(name);
            if (property == null)
                throw new IllegalArgumentException("Unknown property " + name + " on block " + parentBlock.getKey());
            Property.Value<?> propertyValue = property.parse(value);
            propertyValues.put(property, propertyValue);
            System.out.println(propertyValue);
        }

    }

    public <T extends Comparable<T>, V extends T> CustomBlockState with(Property<T> property, V value) {
        Property.Value<?> element = this.propertyValues.get(property);
        if (element == null)
            throw new IllegalArgumentException("Cannot set property " + property.getName() + " as it does not exist in " + parentBlock.getKey());
        if (element.value() == value) {
            return this;
        }
        if (property.getValues().contains(value)) {
            propertyValues.put(property, property.createValue(value));
        } else {
            throw new IllegalArgumentException("Cannot set property " + property.getName() + " as " + value + " is not a valid value");
        }
        return this;
    }

    public <T extends Comparable<T>> T get(Property<T> property) {
        Property.Value<?> value = this.propertyValues.get(property);
        if (value == null) {
            throw new IllegalArgumentException("Cannot get property " + property.getName() + " as it does not exist in " + this.parentBlock.getKey());
        }
        return (T) property.getType().cast(value.value());
    }

    public void saveToEntity(ItemDisplay display) {
        PersistentDataContainer dataContainer = display.getPersistentDataContainer();

        ArrayList<PersistentDataContainer> containers = new ArrayList<>();
        for (Map.Entry<Property<?>, Property.Value<?>> entry : propertyValues.entrySet()) {
            PersistentDataContainer save = dataContainer.getAdapterContext().newPersistentDataContainer();
            String name = entry.getKey().getName();
            String value = entry.getValue().name();

            save.set(NAME_KEY, PersistentDataType.STRING, name);
            save.set(VALUE_KEY, PersistentDataType.STRING, value);
            containers.add(save);
        }
        dataContainer.set(PROPERTIES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, containers.toArray(new PersistentDataContainer[0]));
    }
}
