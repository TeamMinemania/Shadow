package net.shadow.alickgui.element.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.element.Element;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.font.FontRenderers;
import net.shadow.plugin.GlobalConfig;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

public class CategoryDisplay extends Element {
    final List<ModuleDisplay> md = new ArrayList<>();
    final List<ModuleDisplay> amd = new ArrayList<>();
    final ModuleType mt;
    boolean selected = false;
    boolean open = true;
    double current;
    double animProg = 0;
    double lastRender = 0;
    double goal;

    public CategoryDisplay(double x, double y, ModuleType mt) {
        super(x, y, 100, 500);
        this.mt = mt;
        for (Module module : ModuleRegistry.getAll()) {
            if (module.getModuleType() == mt) {
                ModuleDisplay md1 = new ModuleDisplay(0, 0, module);
                md.add(md1);
            }
        }
        this.current = headerHeight() + amd.stream().map(ModuleDisplay::getHeight).reduce(Double::sum).orElse(0d) + 5D;
        this.goal = this.current;
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + headerHeight()) {
            if (button == 0) {
                selected = true;
                return true;
            }
            if (button == 1) {
                open = !open;
                return true;
            }
        } else {
            if (open) {
                for (ModuleDisplay moduleDisplay : amd) {
                    if (moduleDisplay.clicked(x, y, button)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        if (selected) {
            this.popX += deltaX;
            this.popY += deltaY;
            return true;
        } else {
            for (ModuleDisplay moduleDisplay : amd) {
                if (moduleDisplay.dragged(x, y, deltaX, deltaY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean released() {
        selected = false;
        for (ModuleDisplay moduleDisplay : amd) {
            moduleDisplay.released();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        for (ModuleDisplay moduleDisplay : amd) {
            if (moduleDisplay.keyPressed(keycode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    float headerHeight() {
        float padding = 3;
        return padding + FontRenderers.getRenderer().getFontHeight() + padding;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        //        Renderer.R2D.fill(matrices, theme.getHeader(), x, y, x + width, y + headerHeight());
        double r = 5D;
        double texPad = 4;
        double texDim = (FontRenderers.getRenderer().getFontHeight() + 6) - texPad * 2;
        amd.clear();
        String atarg = GlobalConfig.search_term.toLowerCase().strip();
        for (ModuleDisplay m : md) {
            if (m.getModule().getName().toLowerCase().startsWith(atarg)) {
                amd.add(m);
            }
        }
        this.goal = headerHeight() + amd.stream().map(ModuleDisplay::getHeight).reduce(Double::sum).orElse(0d) + r - 20;
        RenderUtils.renderRoundedQuad(matrices, theme.getHeader(), x, y, x + width, y + current, r);
        //if (open) animProg += (System.curthat was stupid onrentTimeMillis() - lastRender) / 300d;
        //else animProg -= (System.currentTimeMillis() - lastRender) / 300d;
        lastRender = System.currentTimeMillis();
        current = easeInOutCubic(animProg) * goal + 20;
        RenderSystem.setShaderTexture(0, mt.getWhere());
        RenderUtils.renderTexture(matrices, x + texPad, y + texPad, texDim, texDim, 0, 0, texDim, texDim, texDim, texDim);
        FontRenderers.getRenderer().drawCenteredString(matrices, mt.getName(), x + width / 2d, y + headerHeight() / 2d - FontRenderers.getRenderer().getFontHeight() / 2d, 0xFFFFFF);
        double y = headerHeight();
        if (current > 35) {
            for (ModuleDisplay moduleDisplay : amd) {
                moduleDisplay.setX(this.x);
                moduleDisplay.setY(this.y + y * easeInOutCubic(animProg));
                moduleDisplay.render(matrices, mouseX, mouseY, scrollBeingUsed);
                y += moduleDisplay.getHeight();
            }
        }
        this.x = TransitionUtils.transition(this.x, this.popX, 5);
        this.y = TransitionUtils.transition(this.y, this.popY, 5);
    }

    @Override
    public void tickAnim() {
        double a = 0.04;
        if (!open) {
            a *= -1;
        }
        animProg += a;
        animProg = MathHelper.clamp(animProg, 0, 1);
        for (ModuleDisplay moduleDisplay : md) {
            moduleDisplay.tickAnim();
        }
    }

    @Override
    public boolean charTyped(char c, int mods) {
        for (ModuleDisplay moduleDisplay : md) {
            if (moduleDisplay.charTyped(c, mods)) {
                return true;
            }
        }
        return false;
    }

    public ModuleType getModuleType() {
        return mt;
    }

    double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }
}