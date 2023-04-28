package net.shadow.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.shadow.Shadow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImGuiManager {
    protected static final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    protected static final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();
    private static boolean init = false;

    public static ImGuiImplGl3 getImplGl3() {
        return implGl3;
    }

    public static ImGuiImplGlfw getImplGlfw() {
        return implGlfw;
    }

    private static byte[] getMainFont() {
        try {
            return Files.readAllBytes(Paths.get(ProxyScreen.class.getClassLoader().getResource("Mono.ttf").toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void style() {
        //        ImGui.getStyle().setWindowRounding(6f);
        ImGui.getStyle().setWindowPadding(15, 15);
        ImGui.getStyle().setWindowRounding(5.0f);
        ImGui.getStyle().setFramePadding(5, 5);
        ImGui.getStyle().setFrameRounding(4.0f);
        ImGui.getStyle().setItemSpacing(12, 8);
        ImGui.getStyle().setItemInnerSpacing(8, 6);
        ImGui.getStyle().setIndentSpacing(25.0f);
        ImGui.getStyle().setScrollbarSize(15.0f);
        ImGui.getStyle().setScrollbarRounding(9.0f);
        ImGui.getStyle().setGrabMinSize(5.0f);
        ImGui.getStyle().setGrabRounding(3.0f);

        ImGui.getStyle().setColor(ImGuiCol.Text, 0.80f, 0.80f, 0.83f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TextDisabled, 0.24f, 0.23f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.06f, 0.05f, 0.07f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PopupBg, 0.07f, 0.07f, 0.09f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Border, 0.80f, 0.80f, 0.83f, 0.88f);
        ImGui.getStyle().setColor(ImGuiCol.BorderShadow, 0.92f, 0.91f, 0.88f, 0.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBg, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgHovered, 0.24f, 0.23f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgActive, 0.56f, 0.56f, 0.58f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBg, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgCollapsed, 1.00f, 0.98f, 0.95f, 0.75f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgActive, 0.07f, 0.07f, 0.09f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.MenuBarBg, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarBg, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrab, 0.80f, 0.80f, 0.83f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabHovered, 0.56f, 0.56f, 0.58f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabActive, 0.06f, 0.05f, 0.07f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.CheckMark, 0.80f, 0.80f, 0.83f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrab, 0.80f, 0.80f, 0.83f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrabActive, 0.06f, 0.05f, 0.07f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Button, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, 0.24f, 0.23f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive, 0.56f, 0.56f, 0.58f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Header, 0.10f, 0.09f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderHovered, 0.56f, 0.56f, 0.58f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderActive, 0.06f, 0.05f, 0.07f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGrip, 0.00f, 0.00f, 0.00f, 0.00f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripHovered, 0.56f, 0.56f, 0.58f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripActive, 0.06f, 0.05f, 0.07f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLines, 0.40f, 0.39f, 0.38f, 0.63f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLinesHovered, 0.25f, 1.00f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogram, 0.40f, 0.39f, 0.38f, 0.63f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogramHovered, 0.25f, 1.00f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TextSelectedBg, 0.25f, 1.00f, 0.00f, 0.43f);
    }

    public static void init() {
        if(init){
            return;
        }

        init = true;
        long win = Shadow.c.getWindow().getHandle();
        ImGui.createContext();
        initFonts();
        style();
        implGlfw.init(win, true);
        implGl3.init();
        ImGui.getIO().setConfigWindowsMoveFromTitleBarOnly(true);
        ImGui.getStyle().setWindowMenuButtonPosition(-1);
    }

    public static void initFonts() {
        ImGui.getIO().getFonts().addFontFromMemoryTTF(getMainFont(), 18);
    }
}