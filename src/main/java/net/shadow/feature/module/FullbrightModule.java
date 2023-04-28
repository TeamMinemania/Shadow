package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class FullbrightModule extends Module {
    private static double oldgamma = 0;

    public FullbrightModule() {
        super("Fullbright", "makes it so you can see properly", ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
        oldgamma = Shadow.c.options.gamma;
        Shadow.c.options.gamma = 20F;
    }

    @Override
    public void onDisable() {
        Shadow.c.options.gamma = oldgamma;
    }

    @Override
    public void onUpdate() {
        Shadow.c.options.gamma = 20F;
    }

    @Override
    public void onRender() {

    }
}
