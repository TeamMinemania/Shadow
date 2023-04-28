package net.shadow.alickgui.element.impl.config;


import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

public class DoubleSettingEditor extends ConfigBase<SliderValue> {

    boolean clicked = false;

    public DoubleSettingEditor(double x, double y, double width, SliderValue configValue) {
        super(x, y, width, FontRenderers.getRenderer().getMarginHeight() + 10, configValue);
    }

    void handleClick(double x) {
        double translated = x - this.x;
        double perIn = MathHelper.clamp(translated / width, 0, 1);
        configValue.setValue(Utils.roundToN(perIn * (configValue.getMax() - configValue.getMin()) + configValue.getMin(), configValue.getPrec()));
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            clicked = true;
            if (button == 0) {
                handleClick(x);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        if (clicked) {
            handleClick(x);
        }
        return false;
    }

    @Override
    public boolean released() {
        clicked = false;
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int dump) {
        return false;
    }

    double getPer() {
        return MathHelper.clamp((configValue.getThis() - configValue.getMin()) / (configValue.getMax() - configValue.getMin()), 0, 1);
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        FontRenderers.getRenderer().drawString(matrices, configValue.getKey(), x, y, 0xFFFFFF);
        String t = configValue.getThis().toString();
        FontRenderers.getRenderer().drawString(matrices, t, x + width - FontRenderers.getRenderer().getStringWidth(t) - 1, y, 0xFFFFFF);
        double h = y + FontRenderers.getRenderer().getMarginHeight() + .5; // 9 px left
        RenderUtils.fill(matrices, theme.getInactive(), x, h + 9 / 2d - .5, x + width, h + 9 / 2d);
        RenderUtils.fill(matrices, theme.getActive(), x, h + 9 / 2d - .5, x + width * getPer(), h + 9 / 2d);
        //        Renderer.R2D.fill(matrices, theme.getAccent(), x + width * getPer() - .5, h + .5, x + width * getPer() + .5, h + 8.5);
        RenderUtils.circle2d(x + width * getPer(), h + 9 / 2d, 2, theme.getAccent(), matrices, 10);
    }

    @Override
    public void tickAnim() {

    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
