package net.shadow.feature.module;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.Comparator;

public class HudModule extends Module implements PacketInput {
    public static MultiValue wmm;
    static int lastpacket = 0;
    final CustomValue<String> hudvalue = this.config.create("Name", "Shadow");
    final Identifier LOGO = new Identifier("shadow", "shadowhud.png");
    final Identifier DOGWATER = new Identifier("shadow", "oldlogo.png");
    final BooleanValue rects = this.config.create("Rects", false);
    final BooleanValue rbar = this.config.create("RectBar", false);
    final SliderValue wmsize = this.config.create("Watermark Size", 2, 1, 10, 1);
    final SliderValue tx = this.config.create("Timeout X", 12, 1, 1000, 1);
    final SliderValue ty = this.config.create("Timeout Y", 12, 1, 1000, 1);
    final SliderValue alpha = this.config.create("Alpha", 4, 1, 255, 1);
    final SliderValue trans = this.config.create("Trans", 127, 1, 255, 1);
    final BooleanValue doshowarraylist = this.config.create("Arraylist", true);
    final BooleanValue doshowwatermark = this.config.create("Watermark", true);
    final BooleanValue doshowtimeout = this.config.create("Timeout", false);


    public HudModule() {
        super("Hud", "Shows a few gui elements", ModuleType.RENDER);
        wmm = this.config.create("Texture", "Normal", "Normal", "Old", "Letters");
    }

    public static Module[] reverse(Module[] mlist) {
        for (int i = 0; i < mlist.length / 2; i++) {
            Module temp = mlist[i];
            mlist[i] = mlist[mlist.length - i - 1];
            mlist[mlist.length - i - 1] = temp;
        }
        return mlist;
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {
        MatrixStack matrix = new MatrixStack();
        if (doshowarraylist.getThis()) {

            int ralpha = (int) Math.round(alpha.getThis());
            int moduleOffset = Shadow.c.getWindow().getScaledHeight() - 12;
            Module[] mods = ModuleRegistry.getAll().stream().filter(Module::isEnabled).sorted(Comparator.comparingInt(value -> (int) FontRenderers.getRenderer().getStringWidth(value.getVanityName()))).toArray(Module[]::new);
            for (Module module : reverse(mods)) {
                String w = module.getVanityName();
                int wr = (int) (Shadow.c.getWindow().getScaledWidth() - FontRenderers.getRenderer().getStringWidth(w) - 8);
                if (rbar.getThis()) {
                    RenderUtils.fill(matrix, new Color(ralpha, ralpha, ralpha, 255), Shadow.c.getWindow().getScaledWidth() - 3, moduleOffset - 1, Shadow.c.getWindow().getScaledWidth(), moduleOffset + 12);
                }
                if (rects.getThis()) {
                    RenderUtils.renderRoundedQuad(matrix, new Color(ralpha, ralpha, ralpha, (int) Math.round(trans.getThis())), wr - 2, moduleOffset - 2, Shadow.c.getWindow().getScaledWidth() + 12, moduleOffset + 11, 3);
                }
                FontRenderers.getRenderer().drawString(matrix, module.getVanityName(), wr, moduleOffset - 2.5D, new Color(255, 255, 255, 255).getRGB());
                moduleOffset -= 13;
            }
        }
        if (doshowtimeout.getThis()) {
            int ralpha = (int) Math.round(alpha.getThis());
            if (lastpacket > 120) {
                FontRenderers.getRenderer().drawString(matrix, lastpacket + " ms", (int) Math.round(tx.getThis()), (int) Math.round(ty.getThis()), new Color(ralpha, ralpha, ralpha, 255).getRGB());
            }
            lastpacket++;
        }
        if (doshowwatermark.getThis()) {
            if (wmm.getThis().equalsIgnoreCase("normal")) {
                RenderSystem.setShaderTexture(0, LOGO);
                int i = (int) Math.round(48 * (wmsize.getThis() / 4));
                int j = (int) Math.round(194 * (wmsize.getThis() / 4));
                DrawableHelper.drawTexture(matrix, 3, 3, 0, 0, j, i, j, i);
            } else if (wmm.getThis().equalsIgnoreCase("old")) {
                RenderSystem.setShaderTexture(0, DOGWATER);
                int i = (int) Math.round(48 * (wmsize.getThis() / 4));
                int j = (int) Math.round(194 * (wmsize.getThis() / 4));
                DrawableHelper.drawTexture(matrix, 3, 3, 0, 0, j, i, j, i);
            } else {
                int ralpha = (int) Math.round(alpha.getThis());
                FontRenderers.getRenderer().drawString(matrix, hudvalue.getThis(), 5, 5, new Color(124, 124, 124, 255).getRGB());
            }

        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            lastpacket = 0;
        }
    }
}


