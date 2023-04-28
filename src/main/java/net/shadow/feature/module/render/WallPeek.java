package net.shadow.feature.module.render;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class WallPeek extends Module {

    public WallPeek() {
        super("WallPeek", "Allows you to see through walls", ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        Shadow.c.player.stepHeight = 0.5F;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }
}
