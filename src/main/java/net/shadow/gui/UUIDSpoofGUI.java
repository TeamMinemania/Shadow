package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UUIDSpoofGUI extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    TextFieldWidget whurl;

    protected UUIDSpoofGUI(Text title) {
        super(title);
    }

    private static String loadString(String uri) {
        try {
            URL url = new URL(uri);

            BufferedReader items = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            return items.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        whurl = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 50, 300, 20, Text.of("whurl"));
        whurl.setMaxLength(65535);

        ButtonWidget uuidspoof = new ButtonWidget(ww - 150, hh + 25, 300, 20, Text.of("UUID Spoof"), button -> {

        });

        ButtonWidget namespoof = new ButtonWidget(ww - 150, hh, 300, 20, Text.of("NAME Spoof"), button -> {
        });

        this.addDrawableChild(uuidspoof);
        this.addDrawableChild(namespoof);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 55).getRGB());
        RenderUtils.renderRoundedQuad(matrices, new Color(52, 52, 52, 255), ww - 175, hh - 60, ww + 175, hh + 60, 5);
        whurl.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        whurl.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        whurl.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        whurl.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        whurl.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
