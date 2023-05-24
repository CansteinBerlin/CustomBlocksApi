package de.canstein_berlin.customblocksapi.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.canstein_berlin.customblocksapi.CustomBlocksApiPlugin;
import de.canstein_berlin.customblocksapi.api.CustomBlocksApi;
import de.canstein_berlin.customblocksapi.api.block.CustomBlock;
import de.canstein_berlin.customblocksapi.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
Permission to get items: customblocks.commands.get
 */
public class ListCustomBlocksGUI extends ChestGui {

    private final PaginatedPane pages;
    private final OutlinePane backGround;
    private final StaticPane navigation;
    private final List<CustomBlock> blocks;

    public ListCustomBlocksGUI(Player player) {
        super(6, "§6CustomBlocks", CustomBlocksApiPlugin.getInstance());

        this.blocks = CustomBlocksApi.getInstance().getAllCustomBlocks();

        //Setup Gui
        pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithGuiItems(blocks.stream().map(block -> getGuiItemFromBlock(block, player)).collect(Collectors.toList()));
        addPane(pages);

        //Background
        backGround = new OutlinePane(0, 5, 9, 1);
        backGround.addItem(new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .build()));
        backGround.setRepeat(true);
        backGround.setPriority(Pane.Priority.LOWEST);
        addPane(backGround);

        navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(new ItemBuilder(Material.RED_WOOL).setDisplayName("§cPrevious Page").build(), event ->
        {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
            }

            update();
        }), 0, 0);
        navigation.addItem(new GuiItem(new ItemBuilder(Material.LIME_WOOL).setDisplayName("§aNextPage Page").build(), event ->
        {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
            }
            update();
        }), 8, 0);

        addPane(navigation);

        //Global
        setOnGlobalClick(event -> event.setCancelled(true));
    }

    private GuiItem getGuiItemFromBlock(CustomBlock customBlock, Player player) {

        ItemStack stack = customBlock.getMainPlaceItemStack().clone();
        stack.setAmount(1);
        List<Component> lore = stack.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.clear();
        if (!player.hasPermission("customblocks.commands.get")) {
            lore.add(Component.text("§r§7Click to get"));
            lore.add(Component.text(" "));
        }
        lore.add(Component.text("§r§7Settings:"));
        lore.add(Component.text("§r§7NameSpacedKey:  " + customBlock.getKey().asString()));
        lore.add(Component.text("§r§7Size:                " + "(" + customBlock.getSettings().getWidth() + "/" + customBlock.getSettings().getHeight() + ")"));
        lore.add(Component.text("§r§7Display Material:  " + customBlock.getSettings().getDisplayMaterial()));
        lore.add(Component.text("§r§7Break Instantly:  " + customBlock.getSettings().isBreakInstantly()));
        stack.lore(lore);

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("§r§6" + customBlock.getSettings().getName() + " (" + customBlock.getSettings().getBaseBlock() + ")"));
        stack.setItemMeta(meta);

        GuiItem item = new GuiItem(stack, (inventoryClickEvent -> {
            if (!inventoryClickEvent.getWhoClicked().hasPermission("customblocks.commands.get")) return;

            int amount = 1;
            if (inventoryClickEvent.isShiftClick()) {
                amount = 64;
            }

            ItemStack give = customBlock.getMainPlaceItemStack();
            give.setAmount(amount);
            inventoryClickEvent.getWhoClicked().getInventory().addItem(give);
        }));

        return item;
    }
}
