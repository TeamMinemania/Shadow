package net.shadow.feature.module;

import net.minecraft.entity.EntityPose;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class HitboxRemoverModule extends Module implements PacketOutput {

    final SliderValue offset = this.config.create("Offset", 1, 1, 10, 1);

    public HitboxRemoverModule() {
        super("HitboxRemover", "half your hitbox with elytra", ModuleType.MOVEMENT);
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
        Shadow.c.player.setPose(EntityPose.STANDING);
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet))
            return;

        if (!(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround || packet instanceof PlayerMoveC2SPacket.Full || packet instanceof PlayerMoveC2SPacket.OnGroundOnly))
            return;

        if (Shadow.c.player.input == null) {
            event.cancel();
            return;
        }

        event.cancel();
        double x = packet.getX(0);
        double y = packet.getY(0);
        double z = packet.getZ(0);
        double fuck = offset.getThis() / 10;
        Packet<?> newPacket;
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
            newPacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y + fuck, z, false);
        } else if (packet instanceof PlayerMoveC2SPacket.Full) {
            newPacket = new PlayerMoveC2SPacket.Full(x, y + fuck, z, packet.getYaw(0),
                    packet.getPitch(0), false);
        } else {
            newPacket = new PlayerMoveC2SPacket.OnGroundOnly(false);
        }

        Shadow.c.player.networkHandler.getConnection().send(newPacket);
    }
}
