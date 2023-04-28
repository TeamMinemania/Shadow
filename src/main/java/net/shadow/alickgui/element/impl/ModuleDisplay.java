package net.shadow.alickgui.element.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.element.Element;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.base.Module;
import net.shadow.feature.module.ClickGuiModule;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

public class ModuleDisplay extends Element {
    final Module module;
    final ConfigDisplay cd;
    boolean extended = false;
    double extendAnim = 0;
    long hoverStart = System.currentTimeMillis();
    boolean hoveredBefore = false;

    public ModuleDisplay(double x, double y, Module module) {
        super(x, y, 100, 15);
        this.module = module;
        this.cd = new ConfigDisplay(x, y, module.config);
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            if (button == 0) {
                module.setEnabled(!module.isEnabled()); // left click
            } else if (button == 1) {
                extended = !extended;
            } else {
                return false;
            }
            return true;
        } else {
            return extended && cd.clicked(x, y, button);
        }
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return extended && cd.dragged(x, y, deltaX, deltaY, button);
    }

    @Override
    public boolean released() {
        return extended && cd.released();
    }

    double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    @Override
    public double getHeight() {
        return super.getHeight() + cd.getHeight() * easeInOutCubic(extendAnim);
    }

    public Module getModule() {
        return module;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        return extended && cd.keyPressed(keycode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        boolean hovered = inBounds(mouseX, mouseY);
        if (!hoveredBefore && hovered) {
            hoverStart = System.currentTimeMillis();
        }
        if (hoverStart + 200 < System.currentTimeMillis() && hovered) {
            ClickGUI.instance().renderDescription(Utils.getMouseX(), Utils.getMouseY() + 10, module.getDescription());
        }
        hoveredBefore = hovered;
        RenderUtils.fill(matrices, hovered ? theme.getModule().darker() : theme.getModule(), x, y, x + width, y + height);
        if (module.isEnabled()) {
            RenderUtils.renderRoundedQuad(matrices, theme.getHeader(), x, y, x + width, y + height, 3);
        }
        if (ClickGuiModule.fast()) {
            FontRenderers.getRenderer().drawCenteredString(matrices, module.getName(), x + width / 2d, y + height / 2d - FontRenderers.getRenderer().getMarginHeight() / 2d, 0xFFFFFF);
        } else {
            FontRenderers.getRenderer().drawCenteredString(matrices, module.getName(), x + width / 2d, y + height / 2d - FontRenderers.getRenderer().getMarginHeight() / 2d, 0xFFFFFF);
        }
        cd.setX(this.x);
        cd.setY(this.y + height);
        RenderUtils.beginScissor(matrices, x, y, x + width, y + getHeight());
        if (extendAnim > 0) {
            cd.render(matrices, mouseX, mouseY, scrollBeingUsed);
        }
        RenderUtils.endScissor();
    }

    @Override
    public void tickAnim() {
        double a = 0.04;
        if (!extended) {
            a *= -1;
        }
        extendAnim += a;
        extendAnim = MathHelper.clamp(extendAnim, 0, 1);
        cd.tickAnim();
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return extended && cd.charTyped(c, mods);
    }
}
