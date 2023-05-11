package de.canstein_berlin.customblocksapi.api.render;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import org.bukkit.Axis;

import java.util.HashMap;

/**
 * Builder of the CMDLookupTable.
 */
public class CMDLookupTableBuilder {

    private final CustomBlock parentBlock;
    private HashMap<Property<?>, Property.Value<?>> properties;
    private int customModelData;
    private boolean customModelDataSet;
    private HashMap<Axis, Integer> rotations;


    private CMDLookupTable lookupTable;

    public CMDLookupTableBuilder(CustomBlock parentBlock) {
        this.parentBlock = parentBlock;
        lookupTable = new CMDLookupTable();
        customModelDataSet = false;
        properties = new HashMap<>();
        rotations = new HashMap<>();
    }


    /**
     * Add a new property requirement (Can be chained for one element)
     *
     * @param property Property that is matched
     * @param value    Value the property has to match
     * @param <T>
     * @return Chainable CMDLookupTableBuilder
     */
    public <T extends Comparable<T>> CMDLookupTableBuilder with(Property<T> property, T value) {
        if (parentBlock.getProperties().get(property.getName()) == null)
            throw new IllegalArgumentException("Block " + parentBlock.getSettings().getName() + " does not contain property " + property.getName());

        if (!property.getValues().contains(value))
            throw new IllegalArgumentException("The value " + value + " is not a valid value for the property " + property.getName() + " of block " + parentBlock.getSettings().getName());

        if (properties.containsKey(property))
            throw new IllegalArgumentException("Duplicate property " + property + " on lookuptable of block " + parentBlock.getSettings().getName());

        properties.put(property, property.createValue(value));
        return this;
    }

    /**
     * Set the customModelData that is used if the properties set using with match
     *
     * @param customModelData CustomModelData that is used as the textures
     * @return Chainable CMDLookupTableBuilder
     */
    public CMDLookupTableBuilder hasCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        customModelDataSet = true;
        return this;
    }

    /**
     * The rotation that is applied to the model. Useful for facing property
     *
     * @param axis     Axis around the model is rotated
     * @param rotation Rotation in degrees
     * @return Chainable CMDLookupTableBuilder
     */
    public CMDLookupTableBuilder hasRotation(Axis axis, int rotation) {
        if (rotations.containsKey(axis))
            throw new IllegalArgumentException("Duplicate rotation axis " + axis + " on lookuptable of block " + parentBlock.getSettings().getName());
        if (rotation < 0 || rotation > 360)
            throw new IllegalArgumentException("Rotation out of bounds (0-360) for block " + parentBlock.getSettings().getName());
        rotations.put(axis, rotation);
        return this;
    }

    /**
     * Method to add the previously built element to the table.
     *
     * @return Chainable CMDLookupTableBuilder
     */
    public CMDLookupTableBuilder addElement() {
        if (properties.isEmpty())
            throw new IllegalArgumentException("Attempted to register Lookuptable element with no properties set");
        if (!customModelDataSet)
            throw new IllegalArgumentException("Attempted to register Lookuptable element with no customModelData set");


        lookupTable.getElements().add(new CMDLookupTableElement(properties, customModelData, rotations));
        properties.clear();
        customModelDataSet = false;
        rotations.clear();

        return this;
    }

    public CMDLookupTable build() {
        return lookupTable;
    }
}
