package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class FastPlaceModule extends Module {
    public FastPlaceModule() {
        super("SpeedBuild", "place blocks quickly", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Shadow.clientInterface.setItemUseCooldown(0);
        Shadow.clientInterface.getInteractions().setBlockHitDelay(0);
    }

    @Override
    public void onRender() {

    }
}
