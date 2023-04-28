package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class PacketBlockerModule extends Module implements PacketInput, PacketOutput {
    final BooleanValue doclient = this.config.create("Block Client", false);
    final BooleanValue doserver = this.config.create("Block Server", false);

    public PacketBlockerModule() {
        super("PacketBlocker", "block packets", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
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
        if (doclient.getThis() && !(event.getPacket() instanceof KeepAliveC2SPacket))
            event.cancel();
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (doserver.getThis() && !(event.getPacket() instanceof KeepAliveS2CPacket))
            event.cancel();
    }
}
