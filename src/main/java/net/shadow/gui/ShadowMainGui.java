package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public class ShadowMainGui extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();

    public ShadowMainGui(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        ButtonWidget webhooks = new ButtonWidget(ww - 100, hh + 25, 200, 20, Text.of("Webhooks"), button -> Shadow.c.setScreen(new WebhooksGui(title)));

        ButtonWidget minehut = new ButtonWidget(ww - 100, hh - 25, 200, 20, Text.of("Minehut"), button -> Shadow.c.setScreen(new MinehutGui(title)));

        ButtonWidget sfinder = new ButtonWidget(ww - 100, hh - 50, 200, 20, Text.of("Server"), button -> Shadow.c.setScreen(new ServerInfoGUI(title)));

        //ButtonWidget raidbot = new ButtonWidget(ww - 100, hh, 200, 20, Text.of("Discord"), button -> Shadow.c.setScreen(new RaidBotGui(title)));

        this.addDrawableChild(webhooks);
        this.addDrawableChild(minehut);
        this.addDrawableChild(sfinder);
        //this.addDrawableChild(raidbot);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        RenderUtils.renderRoundedQuad(matrices, new Color(55, 55, 55, 255), ww - 105, hh - 55, ww + 105, hh + 50, 5);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
