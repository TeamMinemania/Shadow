package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

import java.util.Random;
import java.util.Set;

public class DisableAtomicItemLoggerCmd extends Command {
    public DisableAtomicItemLoggerCmd() {
        super("nbtjam", "disable someones atomic item logger :)");
    }

    @Override
    public void call(String[] args) {
        Set<Identifier> i = Registry.ITEM.getIds();
        Random r = new Random();
        for (Identifier ident : i.toArray(Identifier[]::new)) {
            Item destroy = Registry.ITEM.get(ident);
            ItemStack jam = new ItemStack(destroy, 1);
            NbtCompound allah = new NbtCompound();
            allah.put("Allah" + r.nextInt(10000), NbtString.of("Problem?"));
            jam.setNbt(allah);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, jam));
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.AIR, 1)));
        NotificationSystem.notifications.add(new Notification("NBT Jammer", "Jammed NBT", 100));
    }
}
