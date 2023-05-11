package de.canstein_berlin.customblocksapi.api.block.drops;

import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import java.util.Collection;

public interface IDrop {


    Collection<ItemStack> getStacks(LootContext ctx);

}
