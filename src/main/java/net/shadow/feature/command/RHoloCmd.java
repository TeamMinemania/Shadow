package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class RHoloCmd extends Command {
    public RHoloCmd() {
        super("pre", "set conditions for entity spawns with spawn eggs");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 4) {
            ChatUtils.message("Please use the format >pre <pos/vel> <x> <y> <z>");
            ChatUtils.message(args[0]);
            return;
        }
        if (args[0].equalsIgnoreCase("pos")) {
            try {
                ItemStack stack = Shadow.c.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                NbtCompound tag = StringNbtReader.parse("{EntityTag:{Pos:[" + args[1] + ".5," + args[2] + ".0," + args[3] + ".5," + "]}}");
                stack.getNbt().copyFrom(tag);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                ChatUtils.message("Changed Spawning Position");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("vel")) {
            try {
                ItemStack stack = Shadow.c.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                NbtCompound tag = StringNbtReader.parse("{EntityTag:{Motion:[" + args[1] + ".0," + args[2] + ".0," + args[3] + ".0," + "]}}");
                stack.getNbt().copyFrom(tag);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                ChatUtils.message("Changed Spawning Motion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ChatUtils.message("Please use the format >pre <pos/vel> <x> <y> <z>");
        }

    }
}
