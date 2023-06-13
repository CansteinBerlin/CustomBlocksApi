package de.canstein_berlin.customblocksapi.test;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.api.block.properties.BooleanProperty;
import de.canstein_berlin.customblocksapi.api.block.properties.Properties;
import de.canstein_berlin.customblocksapi.api.block.properties.PropertyListBuilder;
import de.canstein_berlin.customblocksapi.api.block.settings.BlockSettings;
import de.canstein_berlin.customblocksapi.api.context.ActionResult;
import de.canstein_berlin.customblocksapi.api.state.CustomBlockState;
import de.canstein_berlin.customblocksapi.api.tick.ITickable;
import de.canstein_berlin.customblocksapi.api.tick.TickState;
import de.canstein_berlin.customblocksapi.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class TestBlockTickable extends CustomBlock implements ITickable {

    private static BooleanProperty ENABLED;

    static {
        ENABLED = Properties.ENABLED;
    }

    public TestBlockTickable(BlockSettings settings) {
        super(settings, 7, new ItemBuilder(Material.DIAMOND).setCustomModelData(6).setDisplayName("§r§6" + settings.getName()).build());
        setDefaultState(getDefaultState().with(ENABLED, false));
    }

    @Override
    public void appendProperties(PropertyListBuilder propertyListBuilder) {
        propertyListBuilder.add(ENABLED);
    }

    @Override
    public void onNeighborUpdate(CustomBlockState state, World world, Location location, CustomBlock block, Location fromPos) {
        state.with(ENABLED, location.getBlock().getBlockPower() > 0);
        state.update();
    }

    @Override
    public ActionResult onUse(CustomBlockState state, World world, Location location, Player player, EquipmentSlot hand) {
        if (!hand.equals(EquipmentSlot.HAND)) return ActionResult.FAIL;

        if (state.get(ENABLED)) {
            player.sendMessage(Component.text("Windows is better then Linux"));
        } else {
            player.sendMessage(Component.text("Even asleep Windows is better then Linux"));
        }

        return ActionResult.FAIL;
    }

    @Override
    public void onTick(TickState state) {
        if (!state.getCustomBlockState().get(ENABLED)) {
            setManualCustomModelData(7, state.getCustomBlockState().getDisplay());
            return;
        }

        if (state.getCount() % 2 == 0) {
            setManualCustomModelData(6, state.getCustomBlockState().getDisplay());
        } else {
            setManualCustomModelData(7, state.getCustomBlockState().getDisplay());
        }
    }

    @Override
    public int getTickDelay() {
        return 10;
    }
}
