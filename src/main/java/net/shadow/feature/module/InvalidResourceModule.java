package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;

public class InvalidResourceModule extends Module {
    SliderValue uwu = this.config.create("Power", 1, 1, 20, 1);
    CustomValue<String> str = this.config.create("Recp", "oak_planks");

    public InvalidResourceModule() {
        super("Data", "crash with auto recipe viewer", ModuleType.CRASH);
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
