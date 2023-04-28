package net.shadow.feature.module;

import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AirJumpModule extends Module {
    public AirJumpModule() {
        super("AirJump", "lets you jump mid-air", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Vec3d velocity = Shadow.c.player.getVelocity();

        if (Shadow.c.options.jumpKey.isPressed() && velocity.y < -0.3)
            Shadow.c.player.jump();
    }

    @Override
    public void onRender() {

    }
}
