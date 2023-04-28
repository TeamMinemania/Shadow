package net.shadow.alickgui.element.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.element.Element;
import net.shadow.alickgui.element.impl.config.*;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.configuration.*;
import net.shadow.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigDisplay extends Element {
    final List<ConfigBase<?>> bases = new ArrayList<>();
    final ModuleSettings mc;
    final double padding = 4;
    final double paddingLeft = 3;
    long hoverStart = System.currentTimeMillis();
    boolean hoveredBefore = false;

    public ConfigDisplay(double x, double y, ModuleSettings mc) {
        super(x, y, 100, 0);
        this.mc = mc;
        for (CustomValue<?> setting : mc.returnThis()) {
            if (setting.getKey().equalsIgnoreCase("Keybind")) {
                KeybindSettingEditor kbinstance = new KeybindSettingEditor(0, 0, width - padding - paddingLeft, (CustomValue<Integer>) setting);
                bases.add(kbinstance);
            }
            if (setting instanceof BooleanValue set) {
                BooleanSettingEditor bse = new BooleanSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(bse);
            } else if (setting instanceof SliderValue set) {
                DoubleSettingEditor dse = new DoubleSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(dse);
            } else if (setting instanceof MultiValue set) {
                MultiValueEditor ese = new MultiValueEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(ese);
            } else if (setting instanceof CustomValue<?>) {
                if (setting.getThis() instanceof String) {
                    StringSettingEditor sse = new StringSettingEditor(0, 0, width - padding - paddingLeft, (CustomValue<String>) setting);
                    bases.add(sse);
                }
            }
        }
        this.height = bases.stream().map(Element::getHeight).reduce(Double::sum).orElse(0d);
    }

    public List<ConfigBase<?>> getBases() {
        return bases;
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.clicked(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.dragged(x, y, deltaX, deltaY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean released() {
        for (ConfigBase<?> basis : bases) {
            basis.released();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.keyPressed(keycode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getHeight() {
        this.height = 4 + getBases().stream().map(Element::getHeight).reduce(Double::sum).orElse(0d);
        return super.getHeight();
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        double yOffset = 2;
        Theme theme = ClickGUI.theme;
        double height = getHeight();
        RenderUtils.fill(matrices, theme.getConfig(), x, this.y, x + width, this.y + height);
        //RenderUtils.fill(matrices, theme.getAccent(), x, this.y, x + 1, this.y + height);
        boolean hovered = inBounds(mouseX, mouseY);
        if (!hoveredBefore && hovered) {
            hoverStart = System.currentTimeMillis();
        }
        hoveredBefore = hovered;
        String renderingDesc = null;
        for (ConfigBase<?> basis : getBases()) {
            basis.setX(x + padding);
            basis.setY(this.y + yOffset);
            basis.render(matrices, 0, 0, 0);
            yOffset += basis.getHeight();
        }
    }

    @Override
    public void tickAnim() {
        for (ConfigBase<?> basis : bases) {
            basis.tickAnim();
        }

    }

    @Override
    public boolean charTyped(char c, int mods) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.charTyped(c, mods)) {
                return true;
            }
        }
        return false;
    }
}
