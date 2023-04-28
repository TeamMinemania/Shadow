package net.shadow.gui.etc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class RoundTextField implements Element, Drawable, Selectable {
    protected final String suggestion;
    final double rad;
    public Runnable changeListener = () -> {
    };
    protected String text = "";
    protected boolean focused;
    protected int cursor;
    protected double textStart;
    protected int selectionStart, selectionEnd;
    boolean mouseOver = false;
    double x, y, width, height;

    public RoundTextField(double x, double y, double width, double height, String text, double rad) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.suggestion = text;
        this.rad = rad;
    }

    public RoundTextField(double x, double y, double width, double height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.suggestion = text;
        this.rad = 5.0;
    }

    protected double maxTextWidth() {
        return width - pad() * 2 - 1;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int preSelectionCursor = 0;
        if (selectionStart < preSelectionCursor && preSelectionCursor == selectionEnd) {
            cursor = selectionStart;
        } else if (selectionEnd > preSelectionCursor && preSelectionCursor == selectionStart) {
            cursor = selectionEnd;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int mods) {
        if (!focused) return false;

        boolean control = MinecraftClient.IS_SYSTEM_MAC ? mods == GLFW.GLFW_MOD_SUPER : mods == GLFW.GLFW_MOD_CONTROL;

        if (control && key == GLFW.GLFW_KEY_C) {
            if (cursor != selectionStart || cursor != selectionEnd) {
                Shadow.c.keyboard.setClipboard(text.substring(selectionStart, selectionEnd));
            }
            return true;
        } else if (control && key == GLFW.GLFW_KEY_X) {
            if (cursor != selectionStart || cursor != selectionEnd) {
                Shadow.c.keyboard.setClipboard(text.substring(selectionStart, selectionEnd));
                clearSelection();
            }

            return true;
        } else if (control && key == GLFW.GLFW_KEY_A) {
            cursor = text.length();
            selectionStart = 0;
            selectionEnd = cursor;
        } else if (mods == ((MinecraftClient.IS_SYSTEM_MAC ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL) | GLFW.GLFW_MOD_SHIFT) && key == GLFW.GLFW_KEY_A) {
            resetSelection();
        } else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            setFocused(false);
            return true;
        }

        return onKeyRepeated(key, mods);
    }

    public boolean onKeyRepeated(int key, int mods) {
        if (!focused) return false;

        boolean control = MinecraftClient.IS_SYSTEM_MAC ? mods == GLFW.GLFW_MOD_SUPER : mods == GLFW.GLFW_MOD_CONTROL;
        boolean shift = mods == GLFW.GLFW_MOD_SHIFT;
        int fuckShit1 = SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_ALT : MinecraftClient.IS_SYSTEM_MAC ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL;
        boolean controlShift = mods == (fuckShit1 | GLFW.GLFW_MOD_SHIFT);
        boolean altShift = mods == ((SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_CONTROL : GLFW.GLFW_MOD_ALT) | GLFW.GLFW_MOD_SHIFT);

        if (control && key == GLFW.GLFW_KEY_V) {
            clearSelection();

            String preText = text;
            String clipboard = Shadow.c.keyboard.getClipboard();
            int addedChars = 0;

            StringBuilder sb = new StringBuilder(text.length() + clipboard.length());
            sb.append(text, 0, cursor);

            for (int i = 0; i < clipboard.length(); i++) {
                char c = clipboard.charAt(i);
                sb.append(c);
                addedChars++;
            }

            sb.append(text, cursor, text.length());

            text = sb.toString();
            cursor += addedChars;
            resetSelection();

            if (!text.equals(preText)) runAction();
            return true;
        } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursor > 0 && cursor == selectionStart && cursor == selectionEnd) {
                String preText = text;

                int count = (mods == fuckShit1)
                        ? cursor
                        : (mods == (SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_CONTROL : GLFW.GLFW_MOD_ALT))
                        ? countToNextSpace(true)
                        : 1;

                text = text.substring(0, cursor - count) + text.substring(cursor);
                cursor -= count;
                resetSelection();

                if (!text.equals(preText)) runAction();
            } else if (cursor != selectionStart || cursor != selectionEnd) {
                clearSelection();
            }

            return true;
        } else {
            boolean fuckShit2 = mods == fuckShit1;
            if (key == GLFW.GLFW_KEY_DELETE) {
                if (cursor < text.length()) {
                    if (cursor == selectionStart && cursor == selectionEnd) {
                        String preText = text;

                        int count = fuckShit2
                                ? text.length() - cursor
                                : (mods == (SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_CONTROL : GLFW.GLFW_MOD_ALT))
                                ? countToNextSpace(false)
                                : 1;

                        text = text.substring(0, cursor) + text.substring(cursor + count);

                        if (!text.equals(preText)) runAction();
                    } else {
                        clearSelection();
                    }
                }

                return true;
            } else if (key == GLFW.GLFW_KEY_LEFT) {
                if (cursor > 0) {
                    if (mods == (SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_CONTROL : GLFW.GLFW_MOD_ALT)) {
                        cursor -= countToNextSpace(true);
                        resetSelection();
                    } else if (fuckShit2) {
                        cursor = 0;
                        resetSelection();
                    } else if (altShift) {
                        if (cursor == selectionEnd && cursor != selectionStart) {
                            cursor -= countToNextSpace(true);
                            selectionEnd = cursor;
                        } else {
                            cursor -= countToNextSpace(true);
                            selectionStart = cursor;
                        }
                    } else if (controlShift) {
                        if (cursor == selectionEnd && cursor != selectionStart) {
                            selectionEnd = selectionStart;
                        }
                        selectionStart = 0;

                        cursor = 0;
                    } else if (shift) {
                        if (cursor == selectionEnd && cursor != selectionStart) {
                            selectionEnd = cursor - 1;
                        } else {
                            selectionStart = cursor - 1;
                        }

                        cursor--;
                    } else {
                        if (cursor == selectionEnd && cursor != selectionStart) {
                            cursor = selectionStart;
                        } else {
                            cursor--;
                        }

                        resetSelection();
                    }

                    cursorChanged();
                } else if (selectionStart != selectionEnd && selectionStart == 0 && mods == 0) {
                    cursor = 0;
                    resetSelection();
                    cursorChanged();
                }

                return true;
            } else if (key == GLFW.GLFW_KEY_RIGHT) {
                if (cursor < text.length()) {
                    if (mods == (SystemUtils.IS_OS_WINDOWS ? GLFW.GLFW_MOD_CONTROL : GLFW.GLFW_MOD_ALT)) {
                        cursor += countToNextSpace(false);
                        resetSelection();
                    } else if (fuckShit2) {
                        cursor = text.length();
                        resetSelection();
                    } else if (altShift) {
                        if (cursor == selectionStart && cursor != selectionEnd) {
                            cursor += countToNextSpace(false);
                            selectionStart = cursor;
                        } else {
                            cursor += countToNextSpace(false);
                            selectionEnd = cursor;
                        }
                    } else if (controlShift) {
                        if (cursor == selectionStart && cursor != selectionEnd) {
                            selectionStart = selectionEnd;
                        }
                        cursor = text.length();
                        selectionEnd = cursor;
                    } else if (shift) {
                        if (cursor == selectionStart && cursor != selectionEnd) {
                            selectionStart = cursor + 1;
                        } else {
                            selectionEnd = cursor + 1;
                        }

                        cursor++;
                    } else {
                        if (cursor == selectionStart && cursor != selectionEnd) {
                            cursor = selectionEnd;
                        } else {
                            cursor++;
                        }

                        resetSelection();
                    }

                    cursorChanged();
                } else if (selectionStart != selectionEnd && selectionEnd == text.length() && mods == 0) {
                    cursor = text.length();
                    resetSelection();
                    cursorChanged();
                }

                return true;
            }
        }

        return false;
    }


    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        clearSelection();
        this.text = text;
        cursor = this.text.length();
        resetSelection();
        runAction();
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!focused) return false;

        clearSelection();

        text = text.substring(0, cursor) + c + text.substring(cursor);

        cursor++;
        resetSelection();

        runAction();
        return true;
    }

    boolean inBounds(double cx, double cy) {
        return cx >= x && cx < x + width && cy >= y && cy < y + height;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        mouseOver = inBounds(Utils.getMouseX(), Utils.getMouseY());
        double pad = pad();
        double overflowWidth = getOverflowWidthForRender();
        double innerHeight = FontRenderers.getRenderer().getFontHeight();
        double centerY = y + height / 2d - innerHeight / 2d;

//        Renderer.R2D.renderQuad(stack,Color.RED,x,y+height,x+width,y+height+.5);
        RenderUtils.renderRoundedQuad(stack, new Color(230, 230, 230), x, y, x + width, y + height, rad);
        RenderUtils.beginScissor(stack, x + pad, y, x + width - pad, y + height);
        // Text content
        if (!text.isEmpty()) {
            FontRenderers.getRenderer().drawString(stack, text, (float) (x + pad - overflowWidth), (float) (centerY), 0, false);
        } else {
            FontRenderers.getRenderer().drawString(stack, suggestion, (float) (x + pad - overflowWidth), (float) (centerY), 0x666666, false);
        }

        // Text highlighting
        if (focused && (cursor != selectionStart || cursor != selectionEnd)) {
            double selStart = x + pad + getTextWidth(selectionStart) - overflowWidth;
            double selEnd = x + pad + getTextWidth(selectionEnd) - overflowWidth;
            RenderUtils.fill(stack, new Color(50, 50, 255, 100), selStart, centerY, selEnd, centerY + FontRenderers.getRenderer().getMarginHeight());
        }

        boolean renderCursor = (System.currentTimeMillis() % 1000) / 500d > 1;
        if (focused && renderCursor) {
            RenderUtils.fill(stack, Color.BLACK, x + pad + getTextWidth(cursor) - overflowWidth, centerY, x + pad + getTextWidth(cursor) - overflowWidth + 1, centerY + FontRenderers.getRenderer().getMarginHeight());
        }

        RenderUtils.endScissor();
    }

    private void clearSelection() {
        if (selectionStart == selectionEnd) return;

        String preText = text;

        text = text.substring(0, selectionStart) + text.substring(selectionEnd);

        cursor = selectionStart;
        selectionEnd = cursor;

        if (!text.equals(preText)) runAction();
    }

    private void resetSelection() {
        selectionStart = cursor;
        selectionEnd = cursor;
    }

    private int countToNextSpace(boolean toLeft) {
        int count = 0;
        boolean hadNonSpace = false;

        for (int i = cursor; toLeft ? i >= 0 : i < text.length(); i += toLeft ? -1 : 1) {
            int j = i;
            if (toLeft) j--;

            if (j >= text.length()) continue;
            if (j < 0) break;

            if (hadNonSpace && Character.isWhitespace(text.charAt(j))) break;
            else if (!Character.isWhitespace(text.charAt(j))) hadNonSpace = true;

            count++;
        }

        return count;
    }

    private void runAction() {
        cursorChanged();
        if (changeListener != null) changeListener.run();
    }

    private double textWidth() {
        return FontRenderers.getRenderer().getStringWidth(text);
    }

    private void cursorChanged() {
        double cursor = getCursorTextWidth(-2);
        if (cursor < textStart) {
            textStart -= textStart - cursor;
        }

        cursor = getCursorTextWidth(2);
        if (cursor > textStart + maxTextWidth()) {
            textStart += cursor - (textStart + maxTextWidth());
        }

        textStart = MathHelper.clamp(textStart, 0, Math.max(textWidth() - maxTextWidth(), 0));

        onCursorChanged();
    }

    protected void onCursorChanged() {
    }

    protected double getTextWidth(int pos) {
        if (pos < 0) return 0;
        pos = Math.min(text.length(), pos);
        return FontRenderers.getRenderer().getStringWidth(text.substring(0, pos));
    }

    protected double getCursorTextWidth(int offset) {
        return getTextWidth(cursor + offset);
    }

    protected double getOverflowWidthForRender() {
        return textStart;
    }

    public String get() {
        return text;
    }

    public void set(String text) {
        this.text = text;

        cursor = MathHelper.clamp(cursor, 0, text.length());
        selectionStart = cursor;
        selectionEnd = cursor;

        cursorChanged();
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {

        boolean wasJustFocused = focused && !this.focused;

        this.focused = focused;

        resetSelection();

        if (wasJustFocused) onCursorChanged();
    }

    public void setCursorMax() {
        cursor = text.length();
    }

    double pad() {
        return 4;
    }

    @Override
    public SelectionType getType() {
        return mouseOver ? SelectionType.HOVERED : SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseOver) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (!text.isEmpty()) {
                    text = "";
                    cursor = 0;
                    selectionStart = 0;
                    selectionEnd = 0;

                    runAction();
                }
            }

            setFocused(true);
            return true;
        }

        if (focused) setFocused(false);

        return false;
    }
}