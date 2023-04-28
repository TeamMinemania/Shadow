package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class NoSwingModule extends Module implements PacketOutput {
    public NoSwingModule() {
        super("NoSwing", "no swing", ModuleType.OTHER);
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

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof HandSwingC2SPacket) {
            event.cancel();
        }
    }
}
