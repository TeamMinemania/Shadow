package net.shadow.feature.module.world;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class VanillaFly extends Module {
    public VanillaFly() {
        super("VanillaFly", "very complicated hack", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        Shadow.c.player.getAbilities().allowFlying = false;
    }

    @Override
    public void onUpdate() {
        Shadow.c.player.getAbilities().allowFlying = true;
    }

    @Override
    public void onRender() {

    }
}
