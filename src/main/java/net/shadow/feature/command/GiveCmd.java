package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;


public class GiveCmd extends Command {
    public GiveCmd() {
        super("give", "give any item with nbt");
    }

    @Override
    public void call(String[] args) {
        try {
            if (args.length > 1) {
                Item item = getItem(args[0]);
                int amount = Integer.parseInt(args[1]);
                ItemStack staack = new ItemStack(item, amount);
                args[0] = "";
                args[1] = "";
                if (args.length >= 3) {
                    String nbt = String.join(" ", args);
                    staack.setNbt(parseNBT(nbt));
                }
                CreativeUtils.give(staack);
                ChatUtils.message("Created Item");
            } else {
                ChatUtils.message("Please use the format >give <item> <amount> <nbt>");
            }
        } catch (Exception e) {
            ChatUtils.message("Please hold an item in your hand");
        }
    }

    private Item getItem(String id) {
        try {
            return Registry.ITEM.get(new Identifier(id));

        } catch (Exception e) {
            ChatUtils.message("Invalid Item: " + id);
        }

        return null;
    }

    private NbtCompound parseNBT(String nbt) {
        try {
            return StringNbtReader.parse(nbt);
        } catch (Exception ignored) {

        }
        return null;
    }
}
