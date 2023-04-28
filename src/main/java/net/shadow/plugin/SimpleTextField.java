package net.shadow.plugin;

import com.google.common.collect.Lists;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.clickgui.ClickGUI;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleTextField extends ClickableWidget implements Drawable, Element {

    boolean selected = false;
    String text = "";
    int cursorIndex = 0;
    int rStartIndex = 0;

    public SimpleTextField(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        hovered = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        selected = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!selected) return false;
        if (keyCode == 259) { // backspace
            List<Character> v = new ArrayList<>(Lists.charactersOf(text));
            if (v.size() > 0 && cursorIndex != 0) v.remove(cursorIndex - 1);
            StringBuilder sb = new StringBuilder();
            for (Character character : v) {
                sb.append(character);
            }
            text = sb.toString();
            cursorIndex--;
            if (cursorIndex < 0) cursorIndex = 0;
        } else if (keyCode == 262) { // arrow right
            cursorIndex++;
        } else if (keyCode == 263) { // arrow left
            cursorIndex--;
            if (cursorIndex < 0) cursorIndex = 0;
        } else if (Screen.isPaste(keyCode)) {
            for (char c : MinecraftClient.getInstance().keyboard.getClipboard().toCharArray()) {
                charTyped(c, 69420);
            }
        } else if (Screen.isSelectAll(keyCode)) {
            this.setText("");
        }
        event_onTextChange();
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (cursorIndex < 0) cursorIndex = 0;
        if (selected && SharedConstants.isValidChar(chr)) {
            List<Character> v = new ArrayList<>(Lists.charactersOf(text));
            v.add(cursorIndex, chr);
            StringBuilder sb = new StringBuilder();
            for (Character character : v) {
                sb.append(character);
            }
            text = sb.toString();
            cursorIndex++;
            if (modifiers != 69420) {
                event_onTextChange();
            }
        }
        return selected;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Shadow.c.keyboard.setRepeatEvents(true);
        cursorIndex = MathHelper.clamp(cursorIndex, 0, text.isEmpty() ? 0 : (text.length()));
        RenderUtils.fill(matrices, ClickGUI.INACTIVE, x, y, x + width, y + height);
        while (rStartIndex > cursorIndex) {
            rStartIndex--;
        }
        if (rStartIndex > text.length()) rStartIndex = text.length();
        if (rStartIndex < 0) rStartIndex = 0;
        String v1 = text.substring(rStartIndex);
        while (FontRenderers.getRenderer().getStringWidth(v1) > width) {
            if (v1.isEmpty()) break;
            v1 = v1.substring(0, v1.length() - 1);
        }
        Color c = ClickGUI.INACTIVE;
        Color c1 = new Color((int) Math.floor(Math.abs(255 - c.getRed())), (int) Math.floor(Math.abs(255 - c.getGreen())), (int) Math.floor(Math.abs(255 - c.getBlue())), 255);
        FontRenderers.getRenderer().drawString(matrices, v1, x + 1, y + (height / 2f) - (8 / 2f), c1.getRGB());
        float w = text.isEmpty() ? 0 : FontRenderers.getRenderer().getStringWidth(text.substring(rStartIndex, cursorIndex));

        float v = (System.currentTimeMillis() % 1000) / 1000f;
        double opacity = Math.sin(v * Math.PI);
        if (w > width) {
            w = width - 2;
            rStartIndex++;
        }
        if (selected) {
            RenderUtils.fill(matrices, ClickGUI.HEADER_RET, x + w + 1, y + 1, x + w + 2, y + height - 1);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.cursorIndex = 0;
        this.rStartIndex = 0;
    }

    public void event_onTextChange() {

    }
}
