package net.shadow.gui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;

import java.awt.*;
import java.util.Objects;

public class ItemFormatGui extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    String promp;
    ItemStack i;
    int origs;
    TextFieldWidget feature;

    public ItemFormatGui(Text title, String prompt, ItemStack original, int slot) {
        super(title);
        this.promp = prompt;
        this.i = original;
        this.origs = slot;
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        feature = new TextFieldWidget(MC.textRenderer, ww - 200, hh, 400, 20, Text.of("feature"));
        feature.setMaxLength(65535);

        ButtonWidget submit = new ButtonWidget(ww - 100, hh + 40, 200, 20, Text.of("Submit"), button -> {
            NbtCompound tag = i.getNbt();
            String content = Objects.requireNonNull(tag).asString();
            content = content.replace("sh$desc", feature.getText()).replace("&", "\u00a7");
            try {
                tag = StringNbtReader.parse(content);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            tag.put("isRun", NbtByte.of((byte) 0));
            i.setNbt(tag);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(origs, i));
            Shadow.c.setScreen(null);
        });

        this.addDrawableChild(submit);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 20).getRGB());
        FontRenderers.getRenderer().drawString(matrices, promp, ww - (FontRenderers.getRenderer().getStringWidth(promp) / 2), hh - 30, 16777215);
        feature.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        feature.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        feature.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        feature.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        feature.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
