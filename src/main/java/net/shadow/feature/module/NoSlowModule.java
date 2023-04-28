package net.shadow.feature.module;

import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class NoSlowModule extends Module {
    public NoSlowModule() {
        super("NoSlow", "Prevent Slowdowns", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Shadow.clientInterface.getPlayer().setMovementMultiplier(new Vec3d(0, 0, 0));
    }

    @Override
    public void onRender() {

    }
}
