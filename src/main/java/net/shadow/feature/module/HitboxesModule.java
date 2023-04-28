package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.inter.GConf;

public class HitboxesModule extends Module {
    final SliderValue amount = this.config.create("Amount", 25, 1, 100, 1);

    public HitboxesModule() {
        super("Hitboxes", "da test module", ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        GConf.doHitBox = false;
    }

    @Override
    public void onUpdate() {
        GConf.boxSize = amount.getThis() / 100;
        GConf.doHitBox = true;
    }

    @Override
    public void onRender() {

    }
}
