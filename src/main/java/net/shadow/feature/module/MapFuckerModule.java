package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.Utils;

import java.util.Random;

public class MapFuckerModule extends Module {
    public MapFuckerModule() {
        super("MapNuker", "map nuker wtf how", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        Random r = new Random();
        ItemStack item = Shadow.c.player.getMainHandStack();
        NbtList decals = new NbtList();
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                NbtCompound decal = new NbtCompound();
                decal.putInt("x", (int) (Shadow.c.player.getX() + x * 8));
                decal.putInt("z", (int) (Shadow.c.player.getZ() + z * 8));
                decal.putByte("type", (byte) r.nextInt(26));
                decal.putDouble("rot", 180.0D);
                decal.putString("id", Utils.rndStr(50));
                decals.add(decal);
            }
        }
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                NbtCompound decal = new NbtCompound();
                decal.putInt("x", (int) (Shadow.c.player.getX() + x * 8) - 3);
                decal.putInt("z", (int) (Shadow.c.player.getZ() + z * 8) - 3);
                decal.putByte("type", (byte) r.nextInt(26));
                decal.putDouble("rot", 180.0D);
                decal.putString("id", Utils.rndStr(50));
                decals.add(decal);
            }
        }
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                NbtCompound decal = new NbtCompound();
                decal.putInt("x", (int) (Shadow.c.player.getX() + x * 8) + 2);
                decal.putInt("z", (int) (Shadow.c.player.getZ() + z * 8) + 2);
                decal.putByte("type", (byte) r.nextInt(26));
                decal.putDouble("rot", 180.0D);
                decal.putString("id", Utils.rndStr(50));
                decals.add(decal);
            }
        }
        item.getNbt().put("Decorations", decals);
        this.setEnabled(false);
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, item));
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
