package de.canstein_berlin.customblocksapi.api.block.drops;

import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.Collection;
import java.util.Random;

public class LootTableDrop implements IDrop {

    private LootTable lootTable;
    private Random random;

    private LootTableDrop(Random random, LootTable lootTable) {
        this.lootTable = lootTable;
        this.random = random;
    }

    public static LootTableDrop of(CustomBlock customBlock, LootTable lootTable) {
        return new LootTableDrop(customBlock.getBlockRandom(), lootTable);
    }


    @Override
    public Collection<ItemStack> getStacks(LootContext ctx) {
        return lootTable.populateLoot(random, ctx);
    }
}
