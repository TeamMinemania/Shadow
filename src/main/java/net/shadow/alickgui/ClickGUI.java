package net.shadow.alickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.alickgui.element.Element;
import net.shadow.alickgui.element.impl.CategoryDisplay;
import net.shadow.alickgui.theme.Theme;
import net.shadow.alickgui.theme.impl.BasicTheme;
import net.shadow.clickgui.NibletRenderer;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.module.BlurModule;
import net.shadow.feature.module.ClickGuiModule;
import net.shadow.font.FontRenderers;
import net.shadow.font.adapter.FontAdapter;
import net.shadow.gui.etc.RoundTextField;
import net.shadow.plugin.GlobalConfig;
import net.shadow.plugin.shader.ShaderSystem;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClickGUI extends Screen {
    public static final Theme theme = new BasicTheme();
    public static ClickGUI instance;
    static HashMap<String, Integer[]> presetpos = new HashMap<>();
    final List<Element> elements = new ArrayList<>();
    final List<CategoryDisplay> funny = new ArrayList<>();
    final NibletRenderer real = new NibletRenderer(100);
    String desc = null;
    double descX, descY;
    double scroll = 0;
    double trackedScroll = 0;
    double introAnimation = 0;
    boolean closing = false;


    private ClickGUI() {
        super(Text.of(""));
        initElements();
    }

    public static ClickGUI instance() {
        if (instance == null) {
            instance = new ClickGUI();
        }
        return instance;
    }


    public void handleScreenTick() {
        if (client != null) if (client.currentScreen instanceof ClickGUI) {
            for (Element element : new ArrayList<>(elements)) {
                element.tickAnim();
            }
            if (!closing && ClickGuiModule.bg()) {
                this.real.tickPhysics();
            }
        }
    }

    public static void setHashMap(HashMap<String, Integer[]> e) {
        presetpos = e;
    }

    @Override
    protected void init() {
        closing = false;
        introAnimation = 0;
        GlobalConfig.search_term = "";
        // this.real.particles.clear();
    }

    @Override
    public void close() {
        closing = true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 10;
        double bottomMost = 0;
        for (Element element : elements) {
            double y = element.getY() + element.getHeight();
            bottomMost = Math.max(bottomMost, y);
        }
        bottomMost -= height;
        bottomMost += 5; // leave 5 space between scroll end and deepest element
        bottomMost = Math.max(0, bottomMost);
        scroll = MathHelper.clamp(scroll, 0, bottomMost);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void renderDescription(double x, double y, String text) {
        desc = text;
        descX = x;
        descY = y;
    }

    public List<CategoryDisplay> getElements() {
        return this.funny;
    }

    void initElements() {
        double width = Shadow.c.getWindow().getScaledWidth();
        double x = 5;
        double y = 5;
        double tallestInTheRoom = 0;
        for (ModuleType value : ModuleType.values()) {
            if (presetpos.size() < 3) {
                CategoryDisplay cd = new CategoryDisplay(x, y, value);
                tallestInTheRoom = Math.max(tallestInTheRoom, cd.getHeight());
                x += cd.getWidth() + 5;
                if (x >= width) {
                    y += tallestInTheRoom + 5;
                    tallestInTheRoom = 0;
                    x = 5;
                }
                elements.add(cd);
                funny.add(cd);
            } else {
                Integer[] poses = presetpos.get(value.getName());
                if (poses == null || poses.length < 2) return;
                CategoryDisplay cd = new CategoryDisplay(poses[0], poses[1], value);
                funny.add(cd);
                elements.add(cd);
            }

        }
    }

    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(ModuleRegistry.getByClass(BlurModule.class).isEnabled()) {
            ShaderSystem.BLUR.getEffect().setUniformValue("progress", 1F);
            ShaderSystem.BLUR.render(delta);
        }
        double d = 0.03;
        if (closing) {
            d *= -1;
        }
        introAnimation += d;
        introAnimation = MathHelper.clamp(introAnimation, 0, 1);
        trackedScroll = scroll;
        if (closing) {
            client.setScreen(null);
            return;
        }
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> renderIntern(matrices, mouseX, mouseY, delta));
        if (!closing) {
        }
    }

    void renderIntern(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (Shadow.c.player == null) {
            RenderUtils.fill(matrices, new Color(15, 15, 15, 255), 0, 0, width, height);
        }
        double wid = width / 2d;
        double hei = height / 2d;
        FontAdapter bigAssFr = FontRenderers.getCustomSize(70);
        double tx = wid - bigAssFr.getStringWidth(GlobalConfig.search_term) / 2d;
        double ty = hei - bigAssFr.getMarginHeight() / 2d;
        bigAssFr.drawString(matrices, GlobalConfig.search_term, (float) tx, (float) ty, 0x50FFFFFF, false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        matrices.push();
        matrices.translate(0, 0, -20);
        if (!closing && ClickGuiModule.bg()) {
            real.rebder(matrices);
        }
        matrices.pop();
        matrices.push();
        double intp = easeInOutQuint(introAnimation);
        matrices.translate(0, -trackedScroll, 0);
        mouseY += trackedScroll;
        List<Element> rev = new ArrayList<>(elements);
        Collections.reverse(rev);
        for (Element element : rev) {
            element.render(matrices, mouseX, mouseY, trackedScroll);
        }
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
        if (desc != null) {
            double width = FontRenderers.getRenderer().getStringWidth(desc);
            if (descX + width > Shadow.c.getWindow().getScaledWidth()) {
                descX -= (descX + width - Shadow.c.getWindow().getScaledWidth()) + 4;
            }
            RenderUtils.fill(matrices, new Color(21, 20, 30, 200), descX - 1, descY, descX + width + 3, descY + FontRenderers.getRenderer().getMarginHeight() + 1);
            FontRenderers.getRenderer().drawString(new MatrixStack(), desc, descX, descY, 0xFFFFFF);
            desc = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (Element element : new ArrayList<>(elements)) {
            if (element.clicked(mouseX, mouseY, button)) {
                elements.remove(element);
                elements.add(0, element); // put to front when clicked
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (Element element : elements) {
            element.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseY += trackedScroll;
        for (Element element : elements) {
            if (element.dragged(mouseX, mouseY, deltaX, deltaY, button)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element element : elements) {
            if (element.keyPressed(keyCode, modifiers)) {
                return true;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && GlobalConfig.search_term.length() > 0) {
            GlobalConfig.search_term = GlobalConfig.search_term.substring(0, GlobalConfig.search_term.length() - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE && !GlobalConfig.search_term.isEmpty()) {
            GlobalConfig.search_term = "";
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {

        for (Element element : elements) {
            if (element.charTyped(chr, modifiers)) {
                return true;
            }
        }
        GlobalConfig.search_term += chr;
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

}
