package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class CriticalsModule extends Module implements PacketOutput {
    public CriticalsModule() {
        super("Criticals", "makes you do a critical hit every time", ModuleType.COMBAT);
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

    private void sendPos(double x, double y, double z, boolean onGround) {
        Shadow.c.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
            event.cancel();

            double posX = Shadow.c.player.getX();
            double posY = Shadow.c.player.getY();
            double posZ = Shadow.c.player.getZ();

            sendPos(posX, posY + 0.0625D, posZ, true);
            sendPos(posX, posY, posZ, false);
            sendPos(posX, posY + 1.1E-5D, posZ, false);
            sendPos(posX, posY, posZ, false);

            Shadow.c.player.networkHandler.getConnection().send(packet);
        }

    }
}
