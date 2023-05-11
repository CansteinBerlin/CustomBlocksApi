package de.canstein_berlin.customblocksapi.api.render;

import com.google.common.collect.ImmutableMap;
import de.canstein_berlin.customblocksapi.api.block.properties.Property;
import org.bukkit.Axis;

import java.util.HashMap;
import java.util.Map;

/**
 * Combination of customModelData and rotation instructions
 */
public class CMDLookupTableElement {

    private final ImmutableMap<Property<?>, Property.Value<?>> properties;
    private final int customModelData;
    private final ImmutableMap<Axis, Integer> rotations;

    public CMDLookupTableElement(HashMap<Property<?>, Property.Value<?>> properties, int customModelData, HashMap<Axis, Integer> rotations) {
        this.properties = ImmutableMap.copyOf(properties);
        this.customModelData = customModelData;
        this.rotations = ImmutableMap.copyOf(rotations);
    }

    public ImmutableMap<Property<?>, Property.Value<?>> getProperties() {
        return properties;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public ImmutableMap<Axis, Integer> getRotations() {
        return rotations;
    }

    public int getScore(HashMap<Property<?>, Property.Value<?>> propertyValues) {
        int score = 0;
        for (Map.Entry<Property<?>, Property.Value<?>> entry : properties.entrySet()) {
            Property.Value<?> recvValue = propertyValues.get(entry.getKey());
            if (recvValue == null)
                throw new IllegalArgumentException("Unknown property " + entry.getKey().getName() + " while parsing CustomModelData table");
            boolean isSame = entry.getValue().equals(recvValue);
            if (isSame) score++;
            else score--;
        }
        return score;
    }
}
