package net.shadow.feature.module;

import net.minecraft.util.math.Box;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AntiFlyKickModule extends Module {

    private int timer;

    public AntiFlyKickModule() {
        super("AntiFlyKick", "dont get fly kick", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        timer = -1;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (timer > -1) {
            timer--;
            if ((timer == 0) && !Shadow.c.player.isOnGround()) {
                Shadow.c.player.addVelocity(0, -0.5, 0);
            }
            return;
        }
        timer = 10;
    }

    @Override
    public void onRender() {

    }
}
