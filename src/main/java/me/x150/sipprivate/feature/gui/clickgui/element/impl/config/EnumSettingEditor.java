package me.x150.sipprivate.feature.gui.clickgui.element.impl.config;

import me.x150.sipprivate.config.EnumSetting;
import me.x150.sipprivate.feature.gui.clickgui.Theme;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class EnumSettingEditor extends ConfigBase<EnumSetting<?>> {
    List<EnumSelectorClickable<?>> values = new ArrayList<>();

    public EnumSettingEditor(double x, double y, double width, EnumSetting<?> configValue) {
        super(x, y, width, 0, configValue);
        double h = FontRenderers.getNormal().getFontHeight() + 3;
        for (Enum<?> value : configValue.getValues()) {
            EnumSelectorClickable<?> a = new EnumSelectorClickable<>(this, 0, 0, width - 2, FontRenderers.getNormal().getMarginHeight() + 2, value);
            values.add(a);
            h += a.height;
        }
        this.height = h + 1;
    }

    <T extends Enum<?>> int getColor(T value) {
        return configValue.getValue().equals(value) ? Theme.ACTIVE.getRGB() : Theme.INACTIVE.getRGB();
    }

    @Override public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            for (EnumSelectorClickable<?> value : values) {
                if (value.inBounds(x, y)) {
                    configValue.accept(value.value.name());
                }
            }
            return true;
        }
        return false;
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY) {
        return false;
    }

    @Override public boolean released() {
        return false;
    }

    @Override public boolean keyPressed(int keycode) {
        return false;
    }

    @Override public void render(MatrixStack matrices) {
        FontRenderers.getNormal().drawString(matrices, configValue.name, x, y + 1, 0xFFFFFF);
        double yOffset = FontRenderers.getNormal().getMarginHeight() + 2;
        Renderer.R2D.fill(matrices, Theme.MODULE.darker(), x, y + yOffset, x + width, y + height);
        yOffset += 1;
        for (EnumSelectorClickable<?> value : values) {
            value.x = x + 1;
            value.y = this.y + yOffset;
            value.width = this.width - 2;
            value.render(matrices);
            yOffset += value.height;
        }
        this.height = yOffset + 1;
    }

    @Override public void tickAnim() {

    }

    static class EnumSelectorClickable<T extends Enum<?>> {
        EnumSettingEditor instance;
        double            x, y, width, height;
        T value;

        public EnumSelectorClickable(EnumSettingEditor instance, double x, double y, double width, double height, T value) {
            this.instance = instance;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.value = value;
        }

        void render(MatrixStack stack) {
            FontRenderers.getNormal().drawCenteredString(stack, value.name(), x + width / 2d, y + height / 2d - FontRenderers.getNormal().getMarginHeight() / 2d, instance.getColor(value));
        }

        boolean inBounds(double cx, double cy) {
            return cx >= x && cx <= x + width && cy >= y && cy <= y + height;
        }
    }
}
