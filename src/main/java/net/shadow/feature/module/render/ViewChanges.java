package net.shadow.feature.module.render;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;

public class ViewChanges extends Module {
    static boolean f = false;
    private static double oldfov = 0;
    final BooleanValue nobob = this.config.create("NoBob", false);
    final BooleanValue hurtcam = this.config.create("NoHurtcam", false);
    final BooleanValue fovoverride = this.config.create("Fov Override", false);
    final BooleanValue nofov = this.config.create("Nofov", false);
    final BooleanValue noscram = this.config.create("NoScramble", false);
    final SliderValue customfov = this.config.create("Fov", 100, 1, 360, 1);

    public ViewChanges() {
        super("ViewChanges", "game render tweeks", ModuleType.RENDER);
    }

    public static boolean noscramble() {
        return f;
    }

    @Override
    public String getSpecial() {
        return hurtcam.getThis() + ":" + nobob.getThis();
    }

    @Override
    public void onEnable() {
        oldfov = Shadow.c.options.fov;
    }

    @Override
    public void onDisable() {
        Shadow.c.options.fov = oldfov;
    }

    @Override
    public void onUpdate() {
        f = noscram.getThis();
        if (fovoverride.getThis()) {
            Shadow.c.options.fov = customfov.getThis();
        }
        if (nofov.getThis()) {
            Shadow.c.options.fovEffectScale = 0F;
        }
    }

    @Override
    public void onRender() {

    }
}
