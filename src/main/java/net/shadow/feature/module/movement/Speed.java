package net.shadow.feature.module.movement;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class Speed extends Module {
    static float fovEffectScal = 0;
    static int ticksonground = 0;
    static int ticksjustsneaking = 0;
    final SliderValue speed = this.config.create("Speed", 20, 1, 50, 1);
    final MultiValue mode = this.config.create("Mode", "OnGround", "OnGround", "BHop", "LowHop", "CSGO");

    public Speed() {
        super("Speed", "Gotta go fast", ModuleType.MOVEMENT);
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
        Shadow.c.options.fovEffectScale = fovEffectScal;
    }

    @Override
    public void onUpdate() {
        fovEffectScal = Shadow.c.options.fovEffectScale;
        if (Shadow.c.player == null) return;
        ClientPlayerEntity player = Shadow.c.player;
        switch (mode.getThis().toLowerCase()) {
            case "onground":
                Shadow.c.player.setSprinting(true);
                Shadow.c.options.fovEffectScale = 0F;
                Shadow.c.player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(Float.parseFloat(speed.getThis() + "") / 50);
                break;

            case "bhop":
                Shadow.c.player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "") / 100;
                if (Shadow.c.player.isOnGround() && Shadow.c.player.forwardSpeed != 0) {
                    Shadow.c.player.jump();
                } else if (Shadow.c.player.isOnGround() && Shadow.c.player.sidewaysSpeed != 0) {
                    Shadow.c.player.jump();
                }
                break;

            case "lowhop":
                if (Shadow.c.player.input.movementForward != 0 || Shadow.c.player.input.movementSideways != 0) {
                    Shadow.c.player.setSprinting(true);

                    if (Shadow.c.player.isOnGround()) Shadow.c.player.addVelocity(0, 0.3, 0);

                    if (Shadow.c.player.isOnGround()) return;

                    float sspeed = Float.parseFloat(speed.getThis() + "") / 50;

                    float yaw = Shadow.c.player.getYaw();
                    float forward = 1;

                    if (Shadow.c.player.forwardSpeed < 0) {
                        yaw += 180;
                        forward = -0.5f;
                    } else if (Shadow.c.player.forwardSpeed > 0) forward = 0.5f;

                    if (Shadow.c.player.sidewaysSpeed > 0) yaw -= 90 * forward;
                    if (Shadow.c.player.sidewaysSpeed < 0) yaw += 90 * forward;

                    yaw = (float) Math.toRadians(yaw);

                    Shadow.c.player.setVelocity(-Math.sin(yaw) * sspeed, Shadow.c.player.getVelocity().y, Math.cos(yaw) * sspeed);
                }
                break;

            case "csgo":
                Shadow.c.player.setVelocity(Shadow.c.player.getVelocity().multiply(1.1));
                break;
        }

    }

    @Override
    public void onRender() {

    }
}
