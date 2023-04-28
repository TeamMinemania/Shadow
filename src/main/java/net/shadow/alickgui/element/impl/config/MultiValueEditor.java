package net.shadow.alickgui.element.impl.config;


import net.minecraft.client.util.math.MatrixStack;
import net.shadow.alickgui.ClickGUI;
import net.shadow.alickgui.theme.Theme;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiValueEditor extends ConfigBase<MultiValue> {
    final List<EnumSelectorClickable> values = new ArrayList<>();

    public MultiValueEditor(double x, double y, double width, MultiValue configValue) {
        super(x, y, width, 0, configValue);
        double h = FontRenderers.getRenderer().getFontHeight() + 3;
        for (String value : configValue.getPossible()) {
            EnumSelectorClickable a = new EnumSelectorClickable(this, 0, 0, width - 2, FontRenderers.getRenderer().getMarginHeight() + 2, value);
            values.add(a);
            h += a.height;
        }
        this.height = h + 3;
    }

    int getColor(String value) {
        Theme theme = ClickGUI.theme;
        return configValue.getThis().equals(value) ? theme.getActive().getRGB() : theme.getInactive().getRGB();
    }

    public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            for (EnumSelectorClickable value : values) {
                if (value.inBounds(x, y)) {
                    configValue.setValue(value.value);
                }
            }
            return true;
        }
        return false;
    }

    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return false;
    }

    public boolean released() {
        return false;
    }

    public boolean keyPressed(int keycode, int dump) {
        return false;
    }

    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        double pad = 0;
        FontRenderers.getRenderer().drawString(matrices, configValue.getThis(), x, y + 1, 0xFFFFFF);
        double yOffset = FontRenderers.getRenderer().getMarginHeight() + 2;
        //        Renderer.R2D.fill(matrices, new Color(0, 0, 0, 30), x, y + yOffset, x + width, y + height);
        RenderUtils.renderRoundedQuad(matrices, new Color(0, 0, 20, 60), x, y + yOffset, x + width, y + height - pad, 5);
        yOffset += 1;
        for (EnumSelectorClickable value : values) {
            value.x = x + 1;
            value.y = this.y + yOffset;
            value.width = this.width - 2;
            value.render(matrices);
            yOffset += value.height;
        }
        this.height = yOffset + pad;
    }

    @Override
    public void tickAnim() {

    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    static class EnumSelectorClickable {
        final MultiValueEditor instance;
        final double height;
        final String value;
        double x;
        double y;
        double width;

        public EnumSelectorClickable(MultiValueEditor instance, double x, double y, double width, double height, String value) {
            this.instance = instance;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.value = value;
        }

        void render(MatrixStack stack) {
            FontRenderers.getRenderer().drawCenteredString(stack, value, x + width / 2d, y + height / 2d - FontRenderers.getRenderer().getMarginHeight() / 2d, instance.getColor(value));
        }

        boolean inBounds(double cx, double cy) {
            return cx >= x && cx < x + width && cy >= y && cy < y + height;
        }
    }
}
