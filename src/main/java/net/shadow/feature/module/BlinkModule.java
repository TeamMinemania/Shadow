package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

import java.util.ArrayDeque;

public class BlinkModule extends Module implements PacketOutput {

    private final ArrayDeque<PlayerMoveC2SPacket> packets = new ArrayDeque<>();

    public BlinkModule() {
        super("Blink", "suspend packets", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);

        packets.forEach(p -> Shadow.c.player.networkHandler.sendPacket(p));
        packets.clear();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet))
            return;

        event.cancel();

        PlayerMoveC2SPacket prevPacket = packets.peekLast();

        if (prevPacket != null && packet.isOnGround() == prevPacket.isOnGround()
                && packet.getYaw(-1) == prevPacket.getYaw(-1)
                && packet.getPitch(-1) == prevPacket.getPitch(-1)
                && packet.getX(-1) == prevPacket.getX(-1)
                && packet.getY(-1) == prevPacket.getY(-1)
                && packet.getZ(-1) == prevPacket.getZ(-1))
            return;

        packets.addLast(packet);
    }
}
