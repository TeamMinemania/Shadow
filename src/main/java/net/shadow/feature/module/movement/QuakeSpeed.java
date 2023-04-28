package net.shadow.feature.module.movement;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;

public class QuakeSpeed extends Module {
    BooleanValue uncappedBunnyhop = config.create("Uncapped B-Hop", true);

    SliderValue groundAccelerate = config.create("Ground Accel", 10.0D, 1.0D, 15.0D, 0);
    SliderValue airAccelerate = config.create("Air Accel", 14.0D, 1.0D, 20.0D, 0);

    BooleanValue trimpingEnabled = config.create("Trimping Enabled", true);

    public QuakeSpeed() {
        super("QuakeSpeed", "speed but quake", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
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
