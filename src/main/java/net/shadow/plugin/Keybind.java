package net.shadow.plugin;


import net.minecraft.client.util.InputUtil;
import net.shadow.Shadow;

public class Keybind {
    public final int keycode;
    boolean pressedbefore = false;

    public Keybind(int kc) {
        this.keycode = kc;
    }

    public boolean isHeld() {
        if (keycode < 0)
            return false;
        return InputUtil.isKeyPressed(Shadow.c.getWindow().getHandle(), keycode) && Shadow.c.currentScreen == null;
    }

    public boolean isPressed() {
        if (Shadow.c.currentScreen != null)
            return false;
        if (keycode < 0)
            return false;
        boolean flag1 = InputUtil.isKeyPressed(Shadow.c.getWindow().getHandle(), keycode);
        if (flag1 && !pressedbefore) {
            pressedbefore = true;
            return true;
        }
        if (!flag1)
            pressedbefore = false;
        return false;
    }
}
