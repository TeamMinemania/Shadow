package net.shadow.gui;

import imgui.ImGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.clickgui.NibletRenderer;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public abstract class ProxyScreen extends Screen {
    public static NibletRenderer niblets;
    boolean a;

    public ProxyScreen() {
        super(Text.of(""));
        niblets = new NibletRenderer(100);
        ImGuiManager.init();
        this.a = false;
    }

    protected abstract void startWindow();

    protected abstract void renderInternal();

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderUtils.fill(matrices, new Color(25, 25, 25, 120), 0, 0, Shadow.c.getWindow().getScaledWidth(), Shadow.c.getWindow().getScaledHeight());
        niblets.rebder(matrices);
        niblets.tickPhysics();
        ImGui.getIO().setDisplaySize(Shadow.c.getWindow().getWidth(), Shadow.c.getWindow().getHeight());
        ImGuiManager.getImplGlfw().newFrame();
        ImGui.newFrame();

        if (!a) {
            startWindow();
        }
        a = true;
        renderInternal();

        ImGui.endFrame();
        ImGui.render();
        ImGuiManager.getImplGl3().renderDrawData(ImGui.getDrawData());
    }
}