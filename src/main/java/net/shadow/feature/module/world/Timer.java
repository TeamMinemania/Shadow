package net.shadow.feature.module.world;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class Timer extends Module {
    static boolean v = false;
    static double h = 1;
    final SliderValue va = this.config.create("Speed", 1, 1, 20, 1);

    public Timer() {
        super("Timer", "change the games speed", ModuleType.WORLD);
    }

    public static boolean getState() {
        return v;
    }

    public static double getTime() {
        return h;
    }

    @Override
    public void onEnable() {
        v = true;
    }

    @Override
    public void onDisable() {
        v = false;
    }

    @Override
    public void onUpdate() {
        h = va.getThis();
    }

    @Override
    public void onRender() {

    }
}
