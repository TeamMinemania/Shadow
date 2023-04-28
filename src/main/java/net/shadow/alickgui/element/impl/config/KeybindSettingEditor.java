package net.shadow.alickgui.element.impl.config;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.alickgui.ClickGUI;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.font.FontRenderers;
import net.shadow.plugin.Keybinds;
import net.shadow.utils.RenderUtils;

public class KeybindSettingEditor extends ConfigBase<CustomValue<Integer>> {
    boolean listening = false;
    String message = "None";
    int kc;

    public KeybindSettingEditor(int x, int y, double d, CustomValue<Integer> configValue) {
        super(x, y, d, 12, configValue);
        kc = configValue.getThis();
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y) && button == 0) {
            listening = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return false;
    }

    @Override
    public boolean released() {
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        if (!this.listening) return false;
        System.out.println(keycode);
        String v = ((char) (keycode)) + "";
        if (keycode == 259) {
            listening = false;
            kc = -1;
            configValue.setValue(kc);
            Keybinds.reload();
            return false;
        }
        v = v.toUpperCase();
        char bruh = v.charAt(0);
        kc = bruh;
        listening = false;
        configValue.setValue(kc);
        Keybinds.reload();
        message = String.valueOf(bruh);
        return true;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        if (listening) message = ("...");
        else message = (kc != -1 ? String.valueOf((char) kc) : "None");
        RenderUtils.renderRoundedQuad(matrices, ClickGUI.theme.getAccent(), x, y, x + width, y + height, 3);
        FontRenderers.getRenderer().drawString(matrices, message, x + (width / 2) - FontRenderers.getRenderer().getStringWidth(message) / 2, y + (height / 2 - 9 / 2), 0xFFFFFF);
    }

    @Override
    public void tickAnim() {

    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
