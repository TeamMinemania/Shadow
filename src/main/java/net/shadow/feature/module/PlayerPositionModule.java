package net.shadow.feature.module;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class PlayerPositionModule extends Module implements PacketOutput {
    private static final double[] rotations = new double[2];
    static int r = 0;
    final SliderValue speed = this.config.create("SpinSpeed", 25, 1, 270, 1);
    final MultiValue mode = this.config.create("Mode", "None", "None", "NoPitch", "SkyLook", "Spinbot", "Selector", "AltSelector");

    public PlayerPositionModule() {
        super("SpoofRots", "sets", ModuleType.OTHER);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
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
        switch (mode.getThis().toLowerCase()) {
            case "none" -> {
                rotations[1] = 0.0;
                rotations[0] = 0.0;
            }
            case "nopitch" -> {
                rotations[1] = 90.0;
                rotations[0] = Shadow.c.player.getYaw();
            }
            case "skylook" -> {
                rotations[1] = -90.0;
                rotations[0] = Shadow.c.player.getYaw();
            }
            case "spinbot" -> {
                r += speed.getThis();
                rotations[1] = -90.0;
                rotations[0] = r;
                if (r >= 360) r = 0;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(0, 0, Shadow.c.player.isOnGround()));
            }
            case "selector" -> {
                rotations[1] = -14.0;
                rotations[0] = 69.0;
            }
            case "altselector" -> {
                rotations[1] = 63.0;
                rotations[0] = 86.0;
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (rotations == null) return;
            if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
                return;
            event.cancel();
            double x = packet.getX(0);
            double y = packet.getY(0);
            double z = packet.getZ(0);

            Packet<?> newPacket;
            if (packet instanceof PlayerMoveC2SPacket.Full) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, (float) rotations[0], (float) rotations[1], packet.isOnGround());
            } else {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround((float) rotations[0], (float) rotations[1], packet.isOnGround());
            }

            Shadow.c.player.networkHandler.getConnection().send(newPacket);
        }
    }
}
