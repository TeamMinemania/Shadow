package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.font.FontRenderers;

import java.awt.*;

public class InputScreen extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    TextFieldWidget feature;
    String prompt;
    Runnable call;

    public InputScreen(String prompt, Runnable callback) {
        super(Text.of(""));
        this.prompt = prompt;
        this.call = callback;
    }

    public String getText() {
        return feature.getText();
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        feature = new TextFieldWidget(MC.textRenderer, ww - 200, hh, 400, 20, Text.of("feature"));
        feature.setMaxLength(65535);

        ButtonWidget submit = new ButtonWidget(ww - 100, hh + 40, 200, 20, Text.of("Submit"), button -> {
            call.run();
        });

        this.addDrawableChild(submit);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 20).getRGB());
        FontRenderers.getRenderer().drawString(matrices, prompt, ww - (FontRenderers.getRenderer().getStringWidth(prompt) / 2), hh - 30, 16777215);
        feature.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        feature.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        feature.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        feature.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        feature.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
