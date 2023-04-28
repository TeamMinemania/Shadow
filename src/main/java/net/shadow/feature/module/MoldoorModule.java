package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;

public class MoldoorModule extends Module {
    final CustomValue<String> prefix = this.config.create("Prefix", "asd");

    public MoldoorModule() {
        super("Moldoor", "Custom prefix for Moldoor", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        toggle();
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
