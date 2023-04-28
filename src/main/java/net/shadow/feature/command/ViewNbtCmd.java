package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class ViewNbtCmd extends Command {
    public ViewNbtCmd() {
        super("viewnbt", "view the nbt of items");
    }

    @Override
    public void call(String[] args) {
        ClientPlayerEntity player = Shadow.c.player;
        ItemStack stack = player.getInventory().getMainHandStack();
        if (stack.isEmpty()) {
            ChatUtils.message("Please hold an item");
            return;
        }

        NbtCompound tag = stack.getNbt();
        String nbt = tag == null ? "" : tag.asString();

        switch (String.join(" ", args).toLowerCase()) {
            case "" -> ChatUtils.message("NBT: " + nbt);
            case "copy" -> {
                Shadow.c.keyboard.setClipboard(nbt);
                ChatUtils.message("NBT data copied to clipboard.");
            }
            default -> {
                ChatUtils.message("Please use the format >viewnbt <?copy>");
                return;
            }
        }
    }
}
