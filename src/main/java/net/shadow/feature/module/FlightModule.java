package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class FlightModule extends Module implements PacketOutput {
    static int ticks;
    static boolean isenbled = false;
    static int packs;
    static boolean isfast = false;
    final SliderValue speed = this.config.create("Speed", 2, 1, 5, 1);
    final BooleanValue autonofall = this.config.create("NoFall", false);
    final BooleanValue autoantiflykick = this.config.create("NoKick", false);
    final MultiValue m = this.config.create("Mode", "Vanilla", "Vanilla", "Glide", "Jetpack", "Velocity", "AirWalk", "Old");

    public FlightModule() {
        super("Flight", "Move around freely", ModuleType.MOVEMENT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }

    @Override
    public void onEnable() {
        isenbled = true;
        Shadow.getEventSystem().add(PacketOutput.class, this);
        if (m.getThis().equalsIgnoreCase("glide")) {
            Shadow.c.player.jump();
        }
    }

    @Override
    public void onDisable() {
        Shadow.c.player.setVelocity(0, 0, 0);
        isenbled = false;
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }//pink monty mole cape

    @Override
    public void onUpdate() {
        isfast = m.getThis().equalsIgnoreCase("fast");
        if (autonofall.getThis()) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        }
        if (m.getThis().equalsIgnoreCase("old")) {
            ClientPlayerEntity player = Shadow.c.player;

            player.getAbilities().flying = false;
            player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "");

            player.setVelocity(0, 0, 0);
            Vec3d velcity = player.getVelocity();

            if (Shadow.c.options.jumpKey.isPressed())
                player.setVelocity(velcity.add(0, speed.getThis(), 0));

            if (Shadow.c.options.sneakKey.isPressed())
                player.setVelocity(velcity.subtract(0, speed.getThis(), 0));
        } else if (m.getThis().equalsIgnoreCase("glide")) {
            ClientPlayerEntity player = Shadow.c.player;
            Vec3d v = player.getVelocity();

            if (player.isOnGround() || player.isTouchingWater() || player.isInLava()
                    || player.isClimbing() || v.y >= 0)
                return;

            player.setVelocity(v.x, Math.max(v.y, -0.1), v.z);
            player.airStrafingSpeed *= speed.getThis() - 0.2;
        } else if (m.getThis().equalsIgnoreCase("jetpack")) {
            if (Shadow.c.options.jumpKey.isPressed())
                Shadow.c.player.jump();
        } else if (m.getThis().equalsIgnoreCase("velocity")) {
            if (Shadow.c.options.jumpKey.isPressed()) {
                Shadow.c.player.addVelocity(0, speed.getThis() / 4, 0);
            }
        } else if (m.getThis().equalsIgnoreCase("airwalk")) {
            Shadow.c.player.setOnGround(true);
            Vec3d v = Shadow.c.player.getVelocity();
            Shadow.c.player.setVelocity(v.x, 0, v.z);
            Shadow.c.player.setSprinting(true);
            Shadow.c.player.age = -1;
            Shadow.c.player.fallDistance = Integer.MAX_VALUE;
            Shadow.c.player.hurtTime = Integer.MAX_VALUE;
        } else if (m.getThis().equalsIgnoreCase("vanilla")) {
            Shadow.c.player.getAbilities().flying = false;
            double bitx = 0;
            double bity = 0;
            double bitz = 0;

            if (Shadow.c.options.backKey.isPressed()) {
                bitz += speed.getThis();
            }
            if (Shadow.c.options.forwardKey.isPressed()) {
                bitz -= speed.getThis();
            }
            if (Shadow.c.options.leftKey.isPressed()) {
                bitx -= speed.getThis();
            }
            if (Shadow.c.options.rightKey.isPressed()) {
                bitx += speed.getThis();
            }
            if (Shadow.c.options.jumpKey.isPressed()) {
                bity += speed.getThis();
            }
            if (Shadow.c.options.sneakKey.isPressed()) {
                bity -= speed.getThis();
            }
            double sinrads = Math.sin(Math.toRadians(Shadow.c.player.getYaw()));
            double cosrads = Math.cos(Math.toRadians(Shadow.c.player.getYaw()));
            double deltax = (bitz * sinrads) + (bitx * -cosrads);
            double deltaz = (bitz * -cosrads) + (bitx * -sinrads);
            Shadow.c.player.setVelocity(new Vec3d(deltax, bity, deltaz));
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet))
            return;

        if (!(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
            return;

        event.cancel();
        double x = packet.getX(0);
        double y = autoantiflykick.getThis() && !packet.isOnGround() ? packet.getY(0) + (Math.random() / 2) * -1 : packet.getY(0);
        double z = packet.getZ(0);

        boolean onground = autonofall.getThis() || packet.isOnGround();

        Packet<?> newPacket;
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround)
            newPacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onground);
        else
            newPacket = new PlayerMoveC2SPacket.Full(x, y, z, packet.getYaw(0),
                    packet.getPitch(0), onground);

        Shadow.c.player.networkHandler.getConnection().send(newPacket);
    }
}
