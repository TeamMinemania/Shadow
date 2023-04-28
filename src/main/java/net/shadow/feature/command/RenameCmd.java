package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class RenameCmd extends Command {
    public RenameCmd() {
        super("rename", "rename items");
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("you must be in creative mode");
            return;
        }

        if (args.length == 0) {
            ChatUtils.message("Please use the format >rename <name>");
            return;
        }

        StringBuilder message = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++)
            message.append(" ").append(args[i]);

        message = new StringBuilder(message.toString().replace("&", "\u00a7").replace("\u00a7\u00a7", "&"));
        ItemStack item = Shadow.c.player.getInventory().getMainHandStack();

        if (item == null) {
            ChatUtils.message("You must hold an item");
            return;
        }

        item.setCustomName(new LiteralText(message.toString()));
        ChatUtils.message("Renamed item to \"" + message + "\u00a7r\".");
    }
}
