package net.shadow.feature.module.combat;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class Velocity extends Module implements PacketInput {
    public Velocity() {
        super("Velocity", "anti knocc bacc", ModuleType.COMBAT);
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

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getId() == Shadow.c.player.getId()) {
                event.cancel();
            }
        }
    }
}
