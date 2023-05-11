package de.canstein_berlin.customblocksapi.api.block.drops;

import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import java.util.Collection;
import java.util.List;

public class ItemDrop implements IDrop {

    private ItemStack stack;

    private ItemDrop(ItemStack stack) {
        this.stack = stack;
    }

    public static ItemDrop of(ItemStack stack) {
        return new ItemDrop(stack);
    }

    @Override
    public Collection<ItemStack> getStacks(LootContext ctx) {
        return List.of(stack);
    }
}
