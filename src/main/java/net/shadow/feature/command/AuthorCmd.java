package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtString;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class AuthorCmd extends Command {
    public AuthorCmd() {
        super("author", "sets the author of a written book");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"Notch"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            ChatUtils.message("Please use the format >author <author>");
            return;
        }

        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode to do this!");
            return;
        }

        ItemStack heldItem = Shadow.c.player.getInventory().getMainHandStack();
        int heldItemID = Item.getRawId(heldItem.getItem());
        int writtenBookID = Item.getRawId(Items.WRITTEN_BOOK);

        if (heldItemID != writtenBookID) {
            ChatUtils.message("You must hole a written book");
            return;
        }
        String author = String.join(" ", args);
        heldItem.setSubNbt("author", NbtString.of(author));
    }
}