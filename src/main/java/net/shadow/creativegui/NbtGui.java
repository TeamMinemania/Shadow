package net.shadow.creativegui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NbtGui extends Screen {
    static final int LINE_HEIGHT = 10;
    List<String> lines = new ArrayList<>();
    NbtCompound source;
    int cursorX = 0;
    int cursorY = 0;
    double lastCursorPosX = 0;
    double lastCursorPosY = 0;
    double renderCursorPosX = 0;
    double renderCursorPosY = 0;
    String errorMessage = "";
    long lastMsgDisplay = System.currentTimeMillis();
    double scroll = 0;
    double trackedScroll = 0;

    public NbtGui(NbtCompound sourceCompound) {
        super(Text.of(""));
        source = sourceCompound;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 30;
        int height = LINE_HEIGHT * lines.size() + 2;
        scroll = MathHelper.clamp(scroll, 0, Math.max(height - this.height + 20, 0));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    protected void init() {
        {
            String v = NbtHelper.toFormattedString(source);
            lines.clear();
            int maxWidth = width - 115;
            for (String s : v.split("\n")) {
                List<String> splitContent = new ArrayList<>();
                StringBuilder line = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (FontRenderers.getRenderer().getStringWidth(line + " " + c) >= maxWidth) {
                        splitContent.add(line.toString());
                        line = new StringBuilder();
                    }
                    line.append(c);
                }
                splitContent.add(line.toString());
                lines.addAll(splitContent);
            }

            new Thread(() -> {
                while (true) {
                    trackedScroll = scroll;
                    renderCursorPosX = lastCursorPosX;
                    renderCursorPosY = lastCursorPosY;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
        ButtonWidget check = new ButtonWidget(width - 105, 11, 100, 20, Text.of("Check"), button -> {
            try {
                StringNbtReader.parse(String.join("", lines));
                showMessage("§aValid nbt!");
            } catch (CommandSyntaxException e) {
                System.out.println(e.getContext());
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget format = new ButtonWidget(width - 105, 11 + 25, 100, 20, Text.of("Format"), button -> {
            try {
                NbtCompound compound = StringNbtReader.parse(String.join("", lines));

                String v = NbtHelper.toFormattedString(compound);
                lines.clear();
                int maxWidth = width - 115;
                for (String s : v.split("\n")) {
                    List<String> splitContent = new ArrayList<>();
                    StringBuilder line = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (FontRenderers.getRenderer().getStringWidth(line + " " + c) >= maxWidth) {
                            splitContent.add(line.toString());
                            line = new StringBuilder();
                        }
                        line.append(c);
                    }
                    splitContent.add(line.toString());
                    lines.addAll(splitContent);
                }
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget upload = new ButtonWidget(width - 105, 11 + 25 + 25, 100, 20, Text.of("Save"), button -> {
            try {
                NbtCompound compound = StringNbtReader.parse(String.join("", lines));
                {
                    String v = NbtHelper.toFormattedString(compound);
                    lines.clear();
                    int maxWidth = width - 115;
                    for (String s : v.split("\n")) {
                        List<String> splitContent = new ArrayList<>();
                        StringBuilder line = new StringBuilder();
                        for (char c : s.toCharArray()) {
                            if (FontRenderers.getRenderer().getStringWidth(line + " " + c) >= maxWidth) {
                                splitContent.add(line.toString());
                                line = new StringBuilder();
                            }
                            line.append(c);
                        }
                        splitContent.add(line.toString());
                        lines.addAll(splitContent);
                    }
                }
                NbtCompound currentNbt = Shadow.c.player.getInventory().getMainHandStack().getNbt();
                if (currentNbt.equals(compound)) {
                    showMessage(Formatting.YELLOW + "No difference. Nothing to update");
                    return;
                }
                Shadow.c.player.getInventory().getMainHandStack().setNbt(compound);
                showMessage("§aUpdated item. Open your inv for it to take effect.");
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget loadFromClip = new ButtonWidget(width - 105, 11 + 25 + 25 + 25, 100, 20, Text.of("From clipboard"), button -> {
            String clip = Shadow.c.keyboard.getClipboard();
            try {
                NbtCompound comp = StringNbtReader.parse(clip);
                String v = NbtHelper.toFormattedString(comp);
                lines.clear();
                int maxWidth = width - 115;
                for (String s : v.split("\n")) {
                    List<String> splitContent = new ArrayList<>();
                    StringBuilder line = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (FontRenderers.getRenderer().getStringWidth(line + " " + c) >= maxWidth) {
                            splitContent.add(line.toString());
                            line = new StringBuilder();
                        }
                        line.append(c);
                    }
                    splitContent.add(line.toString());
                    lines.addAll(splitContent);
                }
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT in clipboard. " + e.getContext());
            }
        });
        ButtonWidget saveToClip = new ButtonWidget(width - 105, 11 + 25 + 25 + 25 + 25, 100, 20, Text.of("To clipboard"), button -> {
            try {
                NbtCompound compound = StringNbtReader.parse(String.join("", lines));
                Text v = NbtHelper.toPrettyPrintedText(compound);
                Shadow.c.keyboard.setClipboard(v.getString());
                showMessage("§aCopied!");
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        addDrawableChild(check);
        addDrawableChild(format);
        addDrawableChild(upload);
        addDrawableChild(loadFromClip);
        addDrawableChild(saveToClip);
        super.init();
    }

    void showMessage(String msg) {
        errorMessage = msg;
        lastMsgDisplay = System.currentTimeMillis();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        if (System.currentTimeMillis() - lastMsgDisplay > 6000) {
            errorMessage = "";
        }
        FontRenderers.getRenderer().drawString(matrices, errorMessage, 5, 1, 0xFFFFFF);
        List<String> lines = new ArrayList<>(this.lines); // to make a backup for rendering
        if (lines.size() == 0) {
            lines.add("{");
            lines.add("");
            lines.add("}");
            this.lines = lines;
            cursorY = 1;
        }
        int height = LINE_HEIGHT * lines.size() + 2;
        matrices.push();
        matrices.translate(0, -trackedScroll, 0);
        RenderUtils.fill(matrices, new Color(0, 0, 0, 100), 5, 11, width - 110, 11 + height);
        int yOffset = 12;
        for (String line : lines) {
            FontRenderers.getRenderer().drawString(matrices, line, 6, yOffset, 0xFFFFFF);
            yOffset += 10;
        }
        double rCX;
        double rCY;
        if (lines.size() != 0) {
            cursorY = MathHelper.clamp(cursorY, 0, lines.size() - 1); // start from 0 here, gotta shift everything down
            cursorX = MathHelper.clamp(cursorX, 0, lines.get(cursorY).length()); // start from 1 here because fucking substring
            rCX = FontRenderers.getRenderer().getStringWidth(lines.get(cursorY).substring(0, cursorX));
            rCY = cursorY * LINE_HEIGHT;
        } else {
            cursorY = cursorX = 0;
            rCX = rCY = 0;
        }
        lastCursorPosX = rCX;
        lastCursorPosY = rCY;
        rCX = renderCursorPosX;
        rCY = renderCursorPosY;
        RenderUtils.fill(matrices, Color.WHITE, rCX + 6, rCY + 12, rCX + 7, rCY + 11 + LINE_HEIGHT);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.isPaste(keyCode)) {
            String clip = Shadow.c.keyboard.getClipboard();
            for (char c : clip.toCharArray()) {
                charTyped(c, 0);
            }
        } else switch (keyCode) {
            case 262 -> cursorX++;
            case 263 -> cursorX--;
            case 264 -> cursorY++;
            case 265 -> cursorY--;
            case 257 -> {
                lines.add(cursorY + 1, "");
                cursorY++;
            }
            case 259 -> {
                if (lines.size() == 0) break;
                if (cursorX != 0) {
                    String current = lines.get(cursorY);
                    StringBuilder reassembled = new StringBuilder(current);
                    reassembled.deleteCharAt(cursorX - 1);
                    lines.remove(cursorY);
                    lines.add(cursorY, reassembled.toString());
                    cursorX--;
                } else {
                    String backup = lines.get(cursorY);
                    lines.remove(cursorY);
                    cursorY--;
                    String current = lines.get(cursorY);
                    lines.remove(cursorY);
                    lines.add(cursorY, current + backup);
                    cursorX = current.length();
                }
            }
            case 261 -> {
                if (lines.size() != 0 && cursorX < lines.get(cursorY).length()) {
                    String current = lines.get(cursorY);
                    StringBuilder reassembled = new StringBuilder(current);
                    reassembled.deleteCharAt(cursorX);
                    lines.remove(cursorY);
                    lines.add(cursorY, reassembled.toString());
                }
            }
            case 256 -> close();
        }
        if (lines.size() != 0) {
            cursorY = MathHelper.clamp(cursorY, 0, lines.size() - 1); // start from 0 here, gotta shift everything down
            cursorX = MathHelper.clamp(cursorX, 0, lines.get(cursorY).length()); // start from 1 here because fucking substring
        } else {
            cursorY = cursorX = 0;
        }
        return true;

    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (SharedConstants.isValidChar(chr)) {
            StringBuilder b = new StringBuilder(lines.get(cursorY));
            b.insert(cursorX, chr);
            lines.remove(cursorY);
            lines.add(cursorY, b.toString());
            cursorX++;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        if (!lines.isEmpty()) {
            String combined = String.join("", lines);
            try {
                source = StringNbtReader.parse(combined);
            } catch (Exception ignored) {
                return;
            }
        }
        super.tick();
    }
}