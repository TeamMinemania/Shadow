package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public class InfoDisplayModule extends Module implements PacketInput {
    static int tsls;
    double tps = 0;
    long syspackettime = 0;

    public InfoDisplayModule() {
        super("InfoDisplay", "some hud shit", ModuleType.RENDER);
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
        tsls++;
        MatrixStack matrices = new MatrixStack();
        RenderUtils.renderRoundedQuad(matrices, new Color(25, 25, 25, 225), Shadow.c.getWindow().getScaledWidth() - 72, -5, Shadow.c.getWindow().getScaledWidth() + 10, 42, 5);
        RenderUtils.renderRoundedQuad(matrices, new Color(55, 55, 55, 200), Shadow.c.getWindow().getScaledWidth() - 70, -5, Shadow.c.getWindow().getScaledWidth() + 10, 40, 5);
        if (tsls > 100) {
            FontRenderers.getRenderer().drawString(matrices, "LAG:" + (tsls - 1) + "ms", Shadow.c.getWindow().getScaledWidth() - 60, 5, new Color(255, 255, 255, 255).getRGB());
        } else {
            FontRenderers.getRenderer().drawString(matrices, "LAG:0ms", Shadow.c.getWindow().getScaledWidth() - 60, 5, new Color(255, 255, 255, 255).getRGB());
        }
        FontRenderers.getRenderer().drawString(matrices, "TPS:" + (tps == -1 ? "Working.." : tps), Shadow.c.getWindow().getScaledWidth() - 60, 15, new Color(255, 255, 255, 255).getRGB());
        try {
            FontRenderers.getRenderer().drawString(matrices, "PING:" + Shadow.c.getNetworkHandler().getPlayerListEntry(Shadow.c.player.getUuid()).getLatency(), Shadow.c.getWindow().getScaledWidth() - 60, 25, new Color(255, 255, 255, 255).getRGB());
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            tsls = 0;
            syspackettime = System.currentTimeMillis();
            tps = calcTps(System.currentTimeMillis() - syspackettime);
        }
    }


    double calcTps(double n) {
        return (20.0 / Math.max((n - 1000.0) / (500.0), 1.0));
    }
}
