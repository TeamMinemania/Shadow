package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Arrays;

public class InjectCmd extends Command {
    public InjectCmd() {
        super("inject", "add nbt to an item");
    }

    @Override
    public void call(String[] args) {
        ClientPlayerEntity player = Shadow.c.player;

        if (!player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode");
            return;
        }

        if (args.length < 1) {
            ChatUtils.message("Please use the format >inject <nbt>");
            return;
        }

        ItemStack stack = player.getInventory().getMainHandStack();
        add(stack, args);
        if (stack == null) {
            ChatUtils.message("Please use the format >inject <nbt>");
            return;
        }


        Shadow.c.player.networkHandler
                .sendPacket(new CreativeInventoryActionC2SPacket(
                        36 + player.getInventory().selectedSlot, stack));

    }

    private void add(ItemStack stack, String[] args) {
        String nbt = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        nbt = nbt.replace("&", "\u00a7").replace("\u00a7\u00a7", "&");

        if (!stack.hasNbt())
            stack.setNbt(new NbtCompound());

        try {
            NbtCompound tag = StringNbtReader.parse(nbt);
            stack.getNbt().copyFrom(tag);
            ChatUtils.message("Item modified.");

        } catch (CommandSyntaxException e) {
            ChatUtils.message(e.getMessage());
            ChatUtils.message("Incorrect nbt");
        }
    }
}
