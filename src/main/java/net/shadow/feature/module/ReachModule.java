package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class ReachModule extends Module {

    public ReachModule() {
        super("Reach", "REEECH", ModuleType.COMBAT);
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

    @Override
    public String getSpecial() {
        return String.valueOf(this.isEnabled());
    }
}
