package net.shadow.alickgui.theme.impl;

import net.shadow.alickgui.theme.Theme;

import java.awt.*;

public class BasicTheme implements Theme {

    @Override
    public String getName() {
        return "BasicTheme";
    }

    @Override
    public Color getAccent() {
        return new Color(0x3A3A3A);
    }

    @Override
    public Color getHeader() {
        return new Color(0xFF2b2b2b, true);
    }

    @Override
    public Color getModule() {
        return new Color(0xFF424242, true);
    }

    @Override
    public Color getConfig() {
        return new Color(0xFF707070, true);
    }

    @Override
    public Color getActive() {
        return new Color(0xFF151515, true);
    }

    @Override
    public Color getInactive() {
        return new Color(0xFF2b2b2b, true);
    }
}
