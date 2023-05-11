package de.canstein_berlin.customblocksapi.api.render;

import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;

import java.util.ArrayList;

/**
 * Custom Model Data Lookuptable. This maps multiple blockstates to specific rotations and Custom Model Datas
 */
public class CMDLookupTable {

    private final ArrayList<CMDLookupTableElement> elements;

    protected CMDLookupTable() {
        elements = new ArrayList<>();
    }

    public CMDLookupTableElement match(CustomBlockState state) {
        int highestScore = Integer.MIN_VALUE;
        CMDLookupTableElement highestElement = null;

        for (CMDLookupTableElement element : elements) {
            int score = element.getScore(state.getPropertyValues());
            if (score > highestScore) {
                highestScore = score;
                highestElement = element;
            }
        }
        return highestElement;
    }

    protected ArrayList<CMDLookupTableElement> getElements() {
        return elements;
    }
}
