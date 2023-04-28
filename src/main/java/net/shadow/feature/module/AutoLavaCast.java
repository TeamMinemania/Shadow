package net.shadow.feature.module;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AutoLavaCast extends Module {
    int ticks = 0;

    public AutoLavaCast() {
        super("AutoLavacast", "make lavacasts automatically", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 2 == 0) return;
        Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();
        Shadow.c.player.updatePosition(Shadow.c.player.getX() + forward.x, Shadow.c.player.getY() + 1, Shadow.c.player.getZ() + forward.z);
        Shadow.c.interactionManager.interactBlock(Shadow.c.player, Shadow.c.world, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.UP, Shadow.c.player.getBlockPos().offset(Direction.DOWN, 1), true));
    }

    @Override
    public void onRender() {

    }
}
