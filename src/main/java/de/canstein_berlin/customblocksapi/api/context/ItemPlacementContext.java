package de.canstein_berlin.customblocksapi.api.context;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ItemPlacementContext {

    @Nullable
    private final Player player;
    private final Location placementPosition;
    private final EquipmentSlot hand;
    private final World world;
    private final ItemStack stack;
    private final boolean replacesExisting;
    private final BlockFace placedAgainst;

    public ItemPlacementContext(Location placementPosition, @Nullable Player player, EquipmentSlot hand, World world, ItemStack stack, boolean replacesExisting, BlockFace placedAgainst) {
        this.placementPosition = placementPosition;
        this.player = player;
        this.hand = hand;
        this.world = world;
        this.stack = stack;
        this.replacesExisting = replacesExisting;
        this.placedAgainst = placedAgainst;
    }

    public ItemPlacementContext(Player player, EquipmentSlot hand, Location location, boolean replacesExisting, BlockFace placedAgainst) {
        this(location, player, hand, player.getWorld(), player.getInventory().getItem(hand), replacesExisting, placedAgainst);
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public Location getPlacementPosition() {
        return placementPosition;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public World getWorld() {
        return world;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isReplacesExisting() {
        return replacesExisting;
    }

    public BlockFace getPlacedAgainst() {
        return placedAgainst;
    }

    public BlockFace getPlayerHorizontalLookDirection() {
        if (player == null) return BlockFace.NORTH;
        return player.getFacing();
    }

    @Override
    public String toString() {
        return "ItemPlacementContext{" +
                "player=" + player +
                ", placementPosition=" + placementPosition +
                ", hand=" + hand +
                ", world=" + world +
                ", stack=" + stack +
                ", replacesExisting=" + replacesExisting +
                ", placedAgainst=" + placedAgainst +
                '}';
    }
}
