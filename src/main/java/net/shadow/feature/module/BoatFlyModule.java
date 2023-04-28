package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;

public class BoatFlyModule extends Module {

    final BooleanValue noclip = this.config.create("NoClip", false);
    final BooleanValue turn = this.config.create("Turn", true);
    final SliderValue fallSpeed = this.config.create("Assent Speed", 0.1, 0, 1, 1);
    final SliderValue speed = this.config.create("Speed", 1, 0, 3, 1);

    private final Entity boat = null;

    public BoatFlyModule() {
        super("BoatMove", "move with boats", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        try {
            Entity vehicle = Shadow.c.player.getVehicle();
            if (vehicle == null) return;
            if (noclip.getThis()) {
                vehicle.noClip = true;
            }
            Vec3d velocity = vehicle.getVelocity();
            double motionY = Shadow.c.options.jumpKey.isPressed() ? fallSpeed.getThis() : 0;
            vehicle.setVelocity(new Vec3d(velocity.x, motionY, velocity.z));
            if (Shadow.c.options.forwardKey.isPressed()) {
                Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();
                vehicle.setVelocity(forward.x * speed.getThis(), motionY, forward.z * speed.getThis());
            }
            if (turn.getThis()) {
                Shadow.c.player.getVehicle().setYaw(Shadow.c.player.getYaw());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onRender() {

    }
}
