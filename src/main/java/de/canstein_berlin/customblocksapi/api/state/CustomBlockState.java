package de.canstein_berlin.customblocksapi.api.state;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
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
    @Nullable
    private Interaction interaction;
    @Nullable
    private Location location;
    private final HashMap<Property<?>, Property.Value<?>> propertyValues;
    private boolean updated;


    public CustomBlockState(CustomBlock parentBlock, ImmutableMap<String, Property<?>> properties) {
        this.parentBlock = parentBlock;

        propertyValues = new HashMap<>();
        for (Map.Entry<String, Property<?>> element : properties.entrySet()) {
            Property<?> property = element.getValue();
            propertyValues.put(property, property.createDefaultValue());
        }
        updated = true;
        interaction = null;
        display = null;
        location = null;
    }

    public CustomBlockState(CustomBlock parentBlock, ItemDisplay display, @Nullable Interaction interaction) {
        this.parentBlock = parentBlock;
        this.display = display;
        this.interaction = interaction;

        Entity locationEntity = interaction == null ? display : interaction;
        location = locationEntity.getLocation().subtract(parentBlock.getSettings().getWidth() / 2, 0, parentBlock.getSettings().getWidth() / 2);

        propertyValues = new HashMap<>();
        PersistentDataContainer dataContainer = display.getPersistentDataContainer();
        PersistentDataContainer[] elements = dataContainer.get(PROPERTIES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (elements == null) {
            return;
        }
        for (PersistentDataContainer container : elements) {
            String name = container.get(NAME_KEY, PersistentDataType.STRING);
            if (name == null)
                throw new IllegalArgumentException("Malformed property data  on block \"" + parentBlock.getSettings().getName() + "\"");
            String value = container.get(VALUE_KEY, PersistentDataType.STRING);
            if (value == null)
                throw new IllegalArgumentException("Malformed property data  on block \"" + parentBlock.getSettings().getName() + "\"");

            Property<?> property = parentBlock.getProperties().get(name);
            if (property == null)
                throw new IllegalArgumentException("Unknown property " + name + " on block \"" + parentBlock.getSettings().getName() + "\"");
            Property.Value<?> propertyValue = property.parse(value);
            propertyValues.put(property, propertyValue);
        }

        updated = false;
    }

    private CustomBlockState(CustomBlock customBlock, HashMap<Property<?>, Property.Value<?>> properties) {
        this.parentBlock = customBlock;
        this.propertyValues = properties;
    }

    private CustomBlockState(CustomBlock parentBlock, @Nullable ItemDisplay display, @Nullable Interaction interaction, @Nullable Location location, HashMap<Property<?>, Property.Value<?>> propertyValues, boolean updated) {
        this.parentBlock = parentBlock;
        this.display = display;
        this.interaction = interaction;
        this.location = location;
        this.propertyValues = propertyValues;
        this.updated = updated;
    }

    public CustomBlockState clone() {
        if (display != null)
            return new CustomBlockState(parentBlock, display, interaction, location, (HashMap<Property<?>, Property.Value<?>>) Map.copyOf(propertyValues), updated);
        return new CustomBlockState(this.parentBlock, new HashMap<>(Map.copyOf(propertyValues)));
    }

    public <T extends Comparable<T>, V extends T> CustomBlockState with(Property<T> property, V value) {
        Property.Value<?> element = this.propertyValues.get(property);
        if (element == null)
            throw new IllegalArgumentException("Cannot set property " + property.getName() + " as it does not exist in \"" + parentBlock.getSettings().getName() + "\"");
        if (element.value() == value) {
            return this;
        }
        if (property.getValues().contains(value)) {
            propertyValues.put(property, property.createValue(value));
            updated = true;
        } else {
            throw new IllegalArgumentException("Cannot set property " + property.getName() + " as " + value + " is not a valid value");
        }
        return this;
    }

    public <T extends Comparable<T>> T get(Property<T> property) {
        Property.Value<?> value = this.propertyValues.get(property);
        if (value == null) {
            throw new IllegalArgumentException("Cannot get property " + property.getName() + " as it does not exist in \"" + this.parentBlock.getSettings().getName() + "\"");
        }
        return property.getType().cast(value.value());
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

    public HashMap<Property<?>, Property.Value<?>> getPropertyValues() {
        return propertyValues;
    }

    public CustomBlock getParentBlock() {
        return parentBlock;
    }

    public void update() {
        if (!updated) return;
        if (display != null) {
            saveToEntity(display);
            parentBlock.redraw(this, display);
        }
        updated = false;
    }

    public void remove(Location location, boolean shouldDrop) {
        if (display == null) return;
        parentBlock.remove(display, interaction, location, shouldDrop);

    }

    @Nullable
    public ItemDisplay getDisplay() {
        return display;
    }

    @Nullable
    public Interaction getInteraction() {
        return interaction;
    }
}
