package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.MiddleClick;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

public class ClickeventsModule extends Module implements LeftClick, MiddleClick, RightClick {
    public ClickeventsModule() {
        super("ClickEvents", "da test for da click events", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(MiddleClick.class, this);
        Shadow.getEventSystem().add(RightClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(MiddleClick.class, this);
        Shadow.getEventSystem().remove(RightClick.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {
    }

    @Override
    public void onLeftClick(LeftClickEvent e) {
        ChatUtils.message("left clicked");
    }

    @Override
    public void onRightClick(RightClickEvent e) {
        ChatUtils.message("right clicked");

    }

    @Override
    public void onMiddleClick(MiddleClickEvent e) {
        ChatUtils.message("middle clicked");
    }

}


