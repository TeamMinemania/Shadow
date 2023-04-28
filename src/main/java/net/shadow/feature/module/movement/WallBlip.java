package net.shadow.feature.module.movement;

import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class WallBlip extends Module {
    final MultiValue m = this.config.create("Mode", "Spider", "Spider", "Instant", "Velocity", "Jump");
    final SliderValue s = this.config.create("Power", 1, 1, 10, 1);

    public WallBlip() {
        super("WallBlip", "go up walls", ModuleType.MOVEMENT);
    }


    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }


    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        Shadow.c.player.stepHeight = 0.5F;
    }

    @Override
    public void onUpdate() {
        switch (m.getThis().toLowerCase()) {
            case "velocity":
                if (Shadow.c.player.horizontalCollision) {
                    Vec3d velocity = Shadow.c.player.getVelocity();
                    Shadow.c.player.setVelocity(velocity.x, velocity.y + s.getThis() / 10, velocity.z);
                }
                break;

            case "spider":
                if (Shadow.c.player.horizontalCollision) {
                    Vec3d velocity = Shadow.c.player.getVelocity();
                    Shadow.c.player.setVelocity(velocity.x, s.getThis() / 10, velocity.z);
                }
                break;

            case "instant":
                Shadow.c.player.stepHeight = Float.parseFloat(s.getThis() + "");
                break;

            case "jump":
                if (Shadow.c.player.horizontalCollision) {
                    Shadow.c.player.jump();
                }
                break;
        }

    }

    @Override
    public void onRender() {

    }
}
