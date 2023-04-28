package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

import java.util.ArrayDeque;

public class BlinkFlyModule extends Module implements PacketOutput {
    final SliderValue s = this.config.create("Speed", 1, 1, 5, 1);
    private final ArrayDeque<PlayerMoveC2SPacket> packets = new ArrayDeque<>();

    public BlinkFlyModule() {
        super("BlinkFly", "fly in blink", ModuleType.MOVEMENT);
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
        ClientPlayerEntity player = Shadow.c.player;

        player.getAbilities().flying = false;
        player.airStrafingSpeed = Float.parseFloat(s.getThis().toString());

        player.setVelocity(0, 0, 0);
        Vec3d velcity = player.getVelocity();

        if (Shadow.c.options.jumpKey.isPressed())
            player.setVelocity(velcity.add(0, s.getThis(), 0));

        if (Shadow.c.options.sneakKey.isPressed())
            player.setVelocity(velcity.subtract(0, s.getThis(), 0));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
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
        event.cancel();
    }
}
