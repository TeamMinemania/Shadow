package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.shadow.Shadow;
import net.shadow.creativegui.CreativeGUI;
import net.shadow.event.events.MiddleClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class CreativeGUIModule extends Module implements MiddleClick {
    final MultiValue mode = this.config.create("Mode", "Open", "Open", "OnMiddleClick");

    public CreativeGUIModule() {
        super("PlayerGUI", "exploit gui", ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(MiddleClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(MiddleClick.class, this);
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("open")) {
            Shadow.c.setScreen(new CreativeGUI(null));
            toggle();
        }
    }

    @Override
    public void onMiddleClick(MiddleClickEvent event) {
        if (mode.getThis().equalsIgnoreCase("onmiddleclick")) {
            if (!(Shadow.c.crosshairTarget instanceof EntityHitResult)) return;
            Entity target = ((EntityHitResult) Shadow.c.crosshairTarget).getEntity();
            Shadow.c.setScreen(new CreativeGUI(target));
        }
    }
}
