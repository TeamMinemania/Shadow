package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class PacketLimiterBypassModule extends Module implements PacketOutput {
    static int currentsentpackets = 0;
    static int ticks = 0;
    static int movepacketssent = 0;
    static int boatpacketssent = 0;
    final SliderValue max = this.config.create("MaxPPS", 1, 1, 300, 0);

    public PacketLimiterBypassModule() {
        super("NoPacketKick", "dont get kicked for sending too many packets", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 20 == 0) {
            currentsentpackets = 0;
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (currentsentpackets > max.getThis()) {
            if (event.getPacket() instanceof KeepAliveC2SPacket) return;
            event.cancel();
        } else {
            currentsentpackets++;
        }
    }
}
