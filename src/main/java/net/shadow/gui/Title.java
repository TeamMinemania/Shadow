package net.shadow.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.HudModule;
import net.shadow.utils.MSAAFramebuffer;

import java.awt.*;

public class Title extends Screen {
    static MultiplayerScreen INSTANCE;
    final Identifier LOGO = new Identifier("shadow", "shadowhud.png");
    final Identifier DOGWATER = new Identifier("shadow", "oldlogo.png");

    public Title() {
        super(Text.of(""));
    }

    @Override
    protected void init() {
        int jw = Shadow.c.getWindow().getScaledWidth();
        int jh = Shadow.c.getWindow().getScaledHeight();
        int h = jh / 2;
        int w = jw / 2;
        ButtonWidget singleplayer = new ButtonWidget(w - 100, h - 25, 200, 20, Text.of("Singleplayer"), button -> Shadow.c.setScreen(new SelectWorldScreen(this)));

        ButtonWidget settings = new ButtonWidget(w - 100, h + 25, 200, 20, Text.of("Settings"), button -> Shadow.c.setScreen(new OptionsScreen(this, Shadow.c.options)));

        ButtonWidget multiplayer = new ButtonWidget(w - 100, h, 200, 20, Text.of("Multiplayer"), button -> {
            if (INSTANCE == null) {
                INSTANCE = new MultiplayerScreen(this);
            }
            Shadow.c.setScreen(INSTANCE);
        });

        ButtonWidget normalmenu = new ButtonWidget(w, h + 75, 98, 20, Text.of("Normal Menu"), button -> {
            ModuleRegistry.find("Titlescreen").toggle();
            Shadow.c.setScreen(new TitleScreen());
            ModuleRegistry.find("Titlescreen").toggle();
        });

        ButtonWidget functions = new ButtonWidget(w - 100, h + 75, 98, 20, Text.of("Alts"), button -> Shadow.c.setScreen(new AltsGui()));

        ButtonWidget capes = new ButtonWidget(w - 100, h + 100, 98, 20, Text.of("Account"), button -> Shadow.c.setScreen(new CapesGUI(title)));

        ButtonWidget tools = new ButtonWidget(w, h + 100, 98, 20, Text.of("Tools"), button -> Shadow.c.setScreen(new ShadowScreenIMGUI()));

        ButtonWidget copenheimer = new ButtonWidget(w - 100, h + 125, 200, 20, Text.of("Molenheimer"), button -> {
            if (INSTANCE == null) {
                INSTANCE = new MultiplayerScreen(this);
            }
            Shadow.c.setScreen(new MoleScreen(title, INSTANCE));
        });

        ButtonWidget clickgui = new ButtonWidget(w - 100, h + 50, 200, 20, Text.of("ClickGui"), button -> Shadow.c.setScreen(net.shadow.alickgui.ClickGUI.instance()));

        this.addDrawableChild(functions);
        this.addDrawableChild(normalmenu);
        this.addDrawableChild(multiplayer);
        this.addDrawableChild(settings);
        this.addDrawableChild(singleplayer);
        this.addDrawableChild(clickgui);
        this.addDrawableChild(copenheimer);
        this.addDrawableChild(capes);
        this.addDrawableChild(tools);
        super.init();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            int jw = Shadow.c.getWindow().getScaledWidth();
            int jh = Shadow.c.getWindow().getScaledHeight();
            int h = jh / 2;
            int w = jw / 2;
            DrawableHelper.fill(matrix, 0, 0, width, height, new Color(37, 37, 37, 255).getRGB());
            if (HudModule.wmm.getThis().equalsIgnoreCase("normal")) {
                RenderSystem.setShaderTexture(0, LOGO);
                DrawableHelper.drawTexture(matrix, w - 100, h - 100, 0, 0, 256, 64, 256, 64);
            } else {
                RenderSystem.setShaderTexture(0, DOGWATER);
                DrawableHelper.drawTexture(matrix, w - 115, h - 100, 0, 0, 256, 64, 256, 64);
            }
            super.render(matrix, mouseX, mouseY, delta);
        });
    }
}
