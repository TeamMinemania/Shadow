package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AntiHungerModule extends Module implements PacketOutput {
    public AntiHungerModule() {
        super("AntiHunger", "loose hunger slower", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket.Mode m = ((ClientCommandC2SPacket) event.getPacket()).getMode();
            ClientCommandC2SPacket.Mode a = ClientCommandC2SPacket.Mode.START_SPRINTING;
            ClientCommandC2SPacket.Mode b = ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            if (m == a || m == b) {
                event.cancel();
            }
        }
    }
}
