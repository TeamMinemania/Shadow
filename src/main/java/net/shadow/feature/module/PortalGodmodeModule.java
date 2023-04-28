package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class PortalGodmodeModule extends Module implements PacketOutput {
    public PortalGodmodeModule() {
        super("PortalGodmode", "das portal hacer", ModuleType.EXPLOIT);
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
        if (event.getPacket() instanceof TeleportConfirmC2SPacket)
            event.cancel();
    }
}
