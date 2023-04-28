package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class LongJumpModule extends Module {
    final SliderValue length = this.config.create("Length", 2, 1, 5, 1);

    public LongJumpModule() {
        super("LongJump", "longjump working hypixel", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = Shadow.c.player;
        Vec3d velocity = player.getVelocity();
        Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();

        if (Shadow.c.options.jumpKey.isPressed()) {
            if (player.isOnGround()) {
                player.setSprinting(true);
                player.jump();
                player.addVelocity(velocity.x + forward.x * length.getThis() / 2, 0, velocity.z + forward.z * length.getThis() / 2);
                player.addVelocity(0, -0.2, 0);
            }
        }
    }

    @Override
    public void onRender() {

    }
}
