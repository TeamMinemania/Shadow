package net.shadow.feature.module;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.WaterListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class JesusModule extends Module implements WaterListener {
    public JesusModule() {
        super("Jesus", "Walk on water", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(WaterListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(WaterListener.class, this);
    }

    @Override
    public void onUpdate() {
        BlockPos bl = new BlockPos(Shadow.c.player.getX(), Shadow.c.player.getY() + 0.2, Shadow.c.player.getZ());
        if (Shadow.c.player.world.getBlockState(bl).getFluidState().getLevel() != 0) {
            Vec3d h = Shadow.c.player.getVelocity();
            Shadow.c.player.setVelocity(h.x, 0.1, h.z);
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onIsPlayerInWater(IsPlayerInWaterEvent event) {
        event.setInWater(false);
    }
}
