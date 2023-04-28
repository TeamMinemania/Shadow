package net.shadow.feature.module.movement;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class Wavedash extends Module {
    final SliderValue pwr = this.config.create("Power", 10, 1, 50, 1);
    final MultiValue mode = this.config.create("Mode", "Set", "Set", "Add");


    public Wavedash() {
        super("Wavedash", "push yourself forward", ModuleType.MOVEMENT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        double blocks = pwr.getThis() / 10;
        ClientPlayerEntity player = Shadow.c.player;

        Vec3d forward = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
        if (mode.getThis().equalsIgnoreCase("set")) {
            player.setVelocity(forward.x * blocks, forward.y * blocks, forward.z * blocks);
        }
        if (mode.getThis().equalsIgnoreCase("add")) {
            player.addVelocity(forward.x * blocks, forward.y * blocks, forward.z * blocks);
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {
    }
}


