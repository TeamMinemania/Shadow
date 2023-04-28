package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.NormalCube;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.event.events.SolidCube;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.inter.IClientPlayerEntity;
import net.shadow.utils.FakePlayer;

public class FreecamModule extends Module implements PacketOutput, PlayerMove, SolidCube, NormalCube {
    static double x;
    static double y;
    static double z;
    final SliderValue c = this.config.create("Speed", 1, 0, 5, 1);
    private FakePlayer fake;

    public FreecamModule() {
        super("Freecam", "fly around without a body", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        fake = new FakePlayer(Shadow.c.player);
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(SolidCube.class, this);
        Shadow.getEventSystem().add(NormalCube.class, this);
        Shadow.getEventSystem().add(PlayerMove.class, this);
        x = Shadow.c.player.getX();
        y = Shadow.c.player.getY();
        z = Shadow.c.player.getZ();
    }

    @Override
    public void onDisable() {
        fake.despawn();
        Shadow.c.player.updatePosition(x, y, z);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(SolidCube.class, this);
        Shadow.getEventSystem().remove(NormalCube.class, this);
        Shadow.getEventSystem().remove(PlayerMove.class, this);

        Shadow.c.player.setVelocity(0, 0, 0);
        Shadow.c.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        Shadow.c.player.getAbilities().flying = false;
        double bitx = 0;
        double bity = 0;
        double bitz = 0;

        if (Shadow.c.options.backKey.isPressed()) {
            bitz += c.getThis();
        }
        if (Shadow.c.options.forwardKey.isPressed()) {
            bitz -= c.getThis();
        }
        if (Shadow.c.options.leftKey.isPressed()) {
            bitx -= c.getThis();
        }
        if (Shadow.c.options.rightKey.isPressed()) {
            bitx += c.getThis();
        }
        if (Shadow.c.options.jumpKey.isPressed()) {
            bity += c.getThis();
        }
        if (Shadow.c.options.sneakKey.isPressed()) {
            bity -= c.getThis();
        }
        double sinrads = Math.sin(Math.toRadians(Shadow.c.player.getYaw()));
        double cosrads = Math.cos(Math.toRadians(Shadow.c.player.getYaw()));
        double deltax = (bitz * sinrads) + (bitx * -cosrads);
        double deltaz = (bitz * -cosrads) + (bitx * -sinrads);
        Shadow.c.player.setVelocity(new Vec3d(deltax, bity, deltaz));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onIsNormalCube(NormalCubeEvent event) {
        event.cancel();
    }

    @Override
    public void onSolidCube(SetSolidCube event) {
        event.cancel();

    }

    @Override
    public void onPlayerMove(IClientPlayerEntity player) {
        player.setNoClip(true);
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket)
            event.cancel();
        if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
            if (packet.getMode().equals(ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) || packet.getMode().equals(ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY) || packet.getMode().equals(ClientCommandC2SPacket.Mode.START_SPRINTING)) {
                event.cancel();
            }
        }
    }
}
