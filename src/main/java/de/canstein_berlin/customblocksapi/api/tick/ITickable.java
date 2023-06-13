package de.canstein_berlin.customblocksapi.api.tick;

public interface ITickable {

    /**
     * Called when the block is ticked
     */
    void onTick(TickState state);

    /**
     * Method to define the delay between ticks
     *
     * @return The delay between two ticks
     */
    int getTickDelay();
}
