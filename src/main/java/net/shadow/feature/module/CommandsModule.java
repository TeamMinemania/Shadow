package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.GlobalConfig;

public class CommandsModule extends Module {
    static boolean isloaded = false;
    final CustomValue<String> prefix = this.config.create("Prefix", ">");

    public CommandsModule() {
        super("Prefix", "TOGGLE THIS TO UPDATE", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        GlobalConfig.setPrefix(prefix.getThis());
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (!isloaded) {
            GlobalConfig.setPrefix(prefix.getThis());
            isloaded = true;
        }
    }

    @Override
    public void onRender() {

    }
}
