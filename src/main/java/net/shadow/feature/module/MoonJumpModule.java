package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class MoonJumpModule extends Module {
    private static boolean lastonground = false;
    final SliderValue s = this.config.create("Power", 2, 1, 5, 1);
    final MultiValue mode = this.config.create("Mode", "Jump", "Jump", "Gravity");

    public MoonJumpModule() {
        super("Moonjump", "jump to the moon!", ModuleType.MOVEMENT);
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
        if (mode.getThis().equalsIgnoreCase("jump")) {
            if (lastonground && Shadow.c.options.jumpKey.isPressed()) {
                Shadow.c.player.setVelocity(Shadow.c.player.getVelocity().x, s.getThis(), Shadow.c.player.getVelocity().z);
            }
            lastonground = Shadow.c.player.isOnGround();
        } else {
            Shadow.c.player.addVelocity(0, 0.0568000030517578, 0); //number goes to 0x150
        }

    }

    @Override
    public void onRender() {

    }
}

