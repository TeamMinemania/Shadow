package net.shadow.alickgui.element.impl.config;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.font.FontRenderers;
import net.shadow.gui.etc.RoundTextField;

public class StringSettingEditor extends ConfigBase<CustomValue<String>> {
    final RoundTextField input;

    public StringSettingEditor(double x, double y, double width, CustomValue<String> configValue) {
        super(x, y, width, 0, configValue);
        double h = FontRenderers.getRenderer().getFontHeight() + 2;
        input = new RoundTextField(x, y, width, h, configValue.getKey(), 5);
        input.changeListener = () -> configValue.setValue(input.get());
        input.setText(configValue.getThis());
        this.height = h + FontRenderers.getRenderer().getMarginHeight() + 1;
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        return input.mouseClicked(x, y, button);
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
    public boolean keyPressed(int keycode, int dump) {
        return input.keyPressed(keycode, 0, dump);
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        FontRenderers.getRenderer().drawString(matrices, configValue.getKey(), x, y, 0xFFFFFF);
        input.setX(x);
        input.setY(y + FontRenderers.getRenderer().getFontHeight());
        input.render(matrices, (int) mouseX, (int) mouseY, 0);
    }

    @Override
    public void tickAnim() {

    }

    @Override
    public boolean charTyped(char c, int mods) {
        return input.charTyped(c, mods);
    }
}