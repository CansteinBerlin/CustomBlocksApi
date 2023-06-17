package de.canstein_berlin.customblocksapi.api.block;

import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.tick.ITickable;
import de.canstein_berlin.customblocksapi.api.tick.TickState;
import org.bukkit.inventory.ItemStack;

public class SimpleAnimatedBlock extends CustomBlock implements ITickable {

    private final int delay; //The Delay between frames
    private final int[] animationFrames; // The frames the animation should play. The first frame is the idle frame

    public SimpleAnimatedBlock(BlockSettings settings, ItemStack placeItemStack, int delay, int... animationFrames) {
        super(settings, animationFrames[0], placeItemStack);
        this.delay = delay;
        this.animationFrames = animationFrames;
    }

    @Override
    public void onTick(TickState state) {
        if (state.getCustomBlockState().getDisplay() == null) return;
        if (!shouldPlayFrames(state)) state.setCount(0);
        else state.setCount(state.getCount() % animationFrames.length);
        setManualCustomModelData(animationFrames[state.getCount()], state.getCustomBlockState().getDisplay());
    }

    @Override
    public int getTickDelay() {
        return delay;
    }

    /**
     * This method is called every blockTick and decides whether the frames should be cycled or the idle frame is played
     *
     * @param state The current state of the block. The counter variable tells you which frame is currently played
     * @return Whether the animation is played (true) or not (false)
     */
    protected boolean shouldPlayFrames(TickState state) {
        return true;
    }
}
