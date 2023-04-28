package net.shadow.plugin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.gui.ItemFormatGui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BetterItems{
    static final Color[] colors = colors().toArray(Color[]::new);
    static String promp = null;
    static int slot = 0;
    static ItemStack i = null;
    static int color = 0;
    static boolean shouldRender = false;

    private static List<Color> colors() {
        List<Color> colors = new ArrayList<>();
        for (int r = 0; r < 100; r++) colors.add(new Color(r * 255 / 100, 255, 0, 255));
        for (int g = 100; g > 0; g--) colors.add(new Color(255, g * 255 / 100, 0, 255));
        for (int b = 0; b < 100; b++) colors.add(new Color(255, 0, b * 255 / 100, 255));
        for (int r = 100; r > 0; r--) colors.add(new Color(r * 255 / 100, 0, 255, 255));
        for (int g = 0; g < 100; g++) colors.add(new Color(0, g * 255 / 100, 255, 255));
        for (int b = 100; b > 0; b--) colors.add(new Color(0, 255, b * 255 / 100, 255));
        colors.add(new Color(0, 255, 0, 255));
        return colors;
    }


    public static void onUpdate() {
        for (int i = 0; i < 4; i++) {
            ItemStack cur = Shadow.c.player.getInventory().getArmorStack(3 - i);
            if (cur.hasNbt()) {
                if (cur.getNbt().contains("rainbow")) {
                    if (!cur.getNbt().getBoolean("rainbow")) return;
                } else {
                    return;
                }
                if (color >= colors.length - (2 + 1)) {
                    color = 0;
                }
                color = color + (int) Math.round(2);
                try {
                    int shawty = colors[color].getRGB();
                    int rgb = shawty & 0xFFFFFF;
                    cur.getNbt().copyFrom(StringNbtReader.parse("{display:{color:" + rgb + "}}"));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(i + 5, cur));
            }
        }
    }


    public static void onSentPacket(net.shadow.event.events.PacketOutput.PacketOutputEvent event) {
        if (event.getPacket() instanceof CreativeInventoryActionC2SPacket packet) {
            ItemStack item = packet.getItemStack();
            if (item.hasNbt()) {
                NbtCompound tag = item.getNbt();
                if (tag.getByte("isRun") == (byte) 1) {
                    String prompt;
                    if (tag.getString("prompt") == null) {
                        prompt = "NONE";
                    } else {
                        prompt = tag.getString("prompt");
                    }
                    i = item;
                    slot = packet.getSlot();
                    promp = prompt;
                    shouldRender = true;
                    return;
                }
            }
            Shadow.c.getNetworkHandler().getConnection().send(new CreativeInventoryActionC2SPacket(packet.getSlot(), item));
        }
    }

    public static void onRender(float partialTicks, MatrixStack matrix) {
        if(shouldRender){
            Shadow.c.setScreen(new ItemFormatGui(Text.of("itemformat"), promp, i, slot));
            shouldRender = false;
        }
    }

    private Color rainbowStroboscopic(double n) {
        return new Color((int) (Math.sin(n) * 127 + 128), (int) (Math.sin(n + Math.PI / 2) * 127 + 128), (int) (Math.sin(n + Math.PI) * 127 + 128));
    }

    private long invert(int x) {
        return (long) (x - Math.pow(2, 32));
    }
}
