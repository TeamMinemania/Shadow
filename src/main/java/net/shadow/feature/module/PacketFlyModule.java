package net.shadow.feature.module;

import org.apache.commons.codec.binary.StringUtils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.mixin.PlayerPositionLookPacketMixin;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class PacketFlyModule extends Module implements PacketOutput, PacketInput{

    private Vec3d pos;


    public PacketFlyModule() {
        super("PacketFly", "fly with packet", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(PacketInput.class, this);
        pos = Shadow.c.player.getPos();
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        if(this.pos == null){
            this.setEnabled(false);
            return;
        }
        double bitx = 0;
        double bity = 0;
        double bitz = 0;

        if (Shadow.c.options.backKey.isPressed()) {
            bitz += 0.04;
        }
        if (Shadow.c.options.forwardKey.isPressed()) {
            bitz -= 0.04;
        }
        if (Shadow.c.options.leftKey.isPressed()) {
            bitx -= 0.04;
        }
        if (Shadow.c.options.rightKey.isPressed()) {
            bitx += 0.04;
        }
        if (Shadow.c.options.jumpKey.isPressed()) {
            bity += 0.04;
        }
        if (Shadow.c.options.sneakKey.isPressed()) {
            bity -= 0.04;
        }
        double sinrads = Math.sin(Math.toRadians(Shadow.c.player.getYaw()));
        double cosrads = Math.cos(Math.toRadians(Shadow.c.player.getYaw()));
        double deltax = (bitz * sinrads) + (bitx * -cosrads);
        double deltaz = (bitz * -cosrads) + (bitx * -sinrads);
        Vec3d move = new Vec3d(deltax, bity, deltaz);
        pos = pos.add(move);
        Shadow.c.player.updatePositionAndAngles(pos.x, pos.y, pos.z, Shadow.c.player.getYaw(), Shadow.c.player.getPitch());
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, false));
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y - 9000, pos.z, true));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        //strip look vectors from all packets
        if(event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround){
            event.cancel();
        }
        if(event.getPacket() instanceof PlayerMoveC2SPacket.Full packet){
            event.cancel();
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(packet.getX(0), packet.getY(0), packet.getZ(0), packet.isOnGround()));
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if(event.getPacket() instanceof PlayerPositionLookS2CPacket packet){
            ((PlayerPositionLookPacketMixin) packet).setYaw(Shadow.c.player.getYaw());
            ((PlayerPositionLookPacketMixin) packet).setPitch(Shadow.c.player.getPitch());
        }
    }


}
