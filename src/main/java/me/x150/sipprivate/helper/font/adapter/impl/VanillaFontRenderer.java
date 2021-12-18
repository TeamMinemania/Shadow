package me.x150.sipprivate.helper.font.adapter.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import net.minecraft.client.util.math.MatrixStack;

public class VanillaFontRenderer implements FontAdapter {
    @Override public int drawString(MatrixStack matrices, String text, float x, float y, int color) {
        return SipoverPrivate.client.textRenderer.draw(matrices, text, x, y, color);
    }

    @Override public int drawString(MatrixStack matrices, String text, double x, double y, int color) {
        return SipoverPrivate.client.textRenderer.draw(matrices, text, (float) x, (float) y, color);
    }

    @Override public int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        return drawString(matrices, text, x - getStringWidth(text) / 2d, y, color);
    }

    @Override public float getStringWidth(String text) {
        return SipoverPrivate.client.textRenderer.getWidth(text);
    }

    @Override public float getFontHeight() {
        return 8;
    }

    @Override public float getMarginHeight() {
        return 9;
    }

    @Override public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        drawString(matrices, s, x, y, color);
    }
}
