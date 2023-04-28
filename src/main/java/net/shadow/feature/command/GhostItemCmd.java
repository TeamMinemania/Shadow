package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class GhostItemCmd extends Command {
    public GhostItemCmd() {
        super("ghostitem", "create a ghost item");
    }

    @Override
    public void call(String[] args) {

        if (args.length < 2) {
            ChatUtils.message("Please use the format >ghostitem <item> <amount>");
            return;
        }

        int stacksize = Integer.parseInt(args[1]);
        Item item = Registry.ITEM.get(new Identifier(args[0]));
        ItemStack stack = new ItemStack(item, stacksize);
        Shadow.c.player.getInventory().armor.set(3, stack);
    }
}
