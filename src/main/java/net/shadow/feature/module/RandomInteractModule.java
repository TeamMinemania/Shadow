package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class RandomInteractModule extends Module {

    final MultiValue mode = this.config.create("Mode", "Normal", "Normal", "SpamFeet");
    final SliderValue overdrive = this.config.create("Overdrive", 1, 1, 20, 1);
    private final Random random = new Random();

    public RandomInteractModule() {
        super("RandomInteract", "randomly interacts with blocks", ModuleType.WORLD);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("normal")) {
            BlockPos lastPos = null;


            int range = 6;
            int bound = range * 2 + 1;

            BlockPos pos;
            int attempts = 0;

            for (int i = 0; i < overdrive.getThis(); i++) {
                pos = new BlockPos(Shadow.c.player.getPos()).add(
                        random.nextInt(bound) - range, random.nextInt(bound) - range,
                        random.nextInt(bound) - range);
                tryToPlaceBlock(pos);
            }
        } else {
            //assume mode happyfeet
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, new BlockPos(Shadow.c.player.getX(), Shadow.c.player.getY() - 1, Shadow.c.player.getZ()), false)));
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        }

    }

    @Override
    public void onRender() {

    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        BlockHitResult r = new BlockHitResult(new Vec3d(0, 1, 0), Direction.DOWN, pos, false);
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, r));
        return true;
    }
}
