package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

import java.util.Random;

public class PopBobCrashMapModule extends Module {

    final SliderValue size = this.config.create("Size", 100, 100, 10000, 1);
    final CustomValue<Integer> mapid = this.config.create("MapId", 1);
    final CustomValue<String> ccords = this.config.create("Coords", "player");

    public PopBobCrashMapModule() {
        super("LagMap", "generates a popbob crash map", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        ItemStack beforehand = Shadow.c.player.getMainHandStack();
        ItemStack crashmap = new ItemStack(Items.FILLED_MAP, 1);
        NbtCompound crash = new NbtCompound();
        int plx;
        int plz;
        if (ccords.getThis().equalsIgnoreCase("player")) {
            plx = (int) Shadow.c.player.getX();
            plz = (int) Shadow.c.player.getZ();
        } else {
            String[] split = ccords.getThis().split(" ");
            if (split.length < 2) {
                return;
            }
            plx = Integer.parseInt(split[0]);
            plz = Integer.parseInt(split[1]);
        }

        NbtList decals = new NbtList();
        for (int i = 0; i < size.getThis(); i++) {
            NbtCompound decal = new NbtCompound();
            decal.putInt("x", plx);
            decal.putInt("z", plz);
            decal.putByte("type", (byte) 5);
            decal.putDouble("rot", 180.0D);
            decal.putString("id", String.valueOf(new Random().nextInt(Integer.MAX_VALUE)));
            decals.add(decal);
        }
        crash.put("Decorations", decals);
        crash.putInt("map", mapid.getThis());
        crashmap.setNbt(crash);
        new Thread(() -> {
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, crashmap));
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, beforehand));
            ChatUtils.message("Added " + size.getThis() + " Markers to map " + mapid.getThis().toString() + "!");
        }).start();
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
