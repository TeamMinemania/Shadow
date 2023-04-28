package net.shadow.alickgui.element.impl.config;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

public class BooleanSettingEditor extends ConfigBase<BooleanValue> {
    final double rw = 14;
    final double rh = 5;
    final double rid = 4;
    final double margin = .5;
    double animProgress = 0;

    public BooleanSettingEditor(double x, double y, double width, BooleanValue configValue) {
        super(x, y, width, FontRenderers.getRenderer().getFontHeight() + 2, configValue);
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
    public boolean clicked(double x, double y, int button) {
        //        System.out.println(x+", "+y+", "+button);
        if (inBounds(x, y) && button == 0) {
            //            System.out.println("clicked");
            configValue.setValue(!configValue.getThis());
            return true;
        }
        return false;
    }

    double getPreferredX() {
        double smoothAnimProgress = easeInOutCubic(animProgress);
        return MathHelper.lerp(smoothAnimProgress, x + margin, x + rw - rid - margin);
        //        return configValue.getValue() ? x + rw - rid - margin : x + margin;
    }

    //    double xSmooth = -1;


    @Override
    public boolean keyPressed(int keycode, int dump) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        double smoothAnimProgress = easeInOutCubic(animProgress);
        RenderUtils.renderRoundedQuad(matrices, smoothAnimProgress > 0.5 ? theme.getActive() : theme.getInactive(), x, y + height / 2d - rh / 2d, x + rw, y + height / 2d + rh / 2d, rh / 2d);
        double rix = getPreferredX();
        //        Renderer.R2D.fill(matrices, theme.getAccent(), rix, y + height / 2d - rh / 2d + margin, rix + rid, y + height / 2d - rh / 2d + margin + rid);
        RenderUtils.circle2d(rix + rid / 2, y + height / 2d, rid / 2d, theme.getAccent(), matrices, 10);
        //        Renderer.R2D.renderCircle(matrices, Theme.ACCENT,);
        FontRenderers.getRenderer().drawString(matrices, configValue.getKey(), x + rw + 2, y + height / 2d - FontRenderers.getRenderer().getMarginHeight() / 2d, 0xFFFFFF);
    }

    double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    }

    @Override
    public void tickAnim() {
        double a = 0.03;
        if (!configValue.getThis()) {
            a *= -1;
        }
        animProgress += a * 3;
        animProgress = MathHelper.clamp(animProgress, 0, 1);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return false;
    }
}
