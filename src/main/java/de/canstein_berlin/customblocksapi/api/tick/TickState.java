package de.canstein_berlin.customblocksapi.api.tick;

import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;

public class TickState {

    private CustomBlockState customBlockState;
    private int count;


    public TickState(CustomBlockState customBlockState, int count) {
        this.customBlockState = customBlockState;
        this.count = count;
    }

    public void update(CustomBlockState state) {
        this.customBlockState = state;
    }

    public CustomBlockState getCustomBlockState() {
        return customBlockState;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
