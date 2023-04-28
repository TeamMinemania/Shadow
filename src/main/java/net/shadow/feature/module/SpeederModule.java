package net.shadow.feature.module;

import org.apache.commons.codec.binary.StringUtils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.utils.ChatUtils;
import net.shadow.feature.configuration.SliderValue;

public class SpeederModule extends Module{

    SliderValue dist = this.config.create("Distance", 5, 1, 9, 0);
    SliderValue slider = this.config.create("Packets", 5, 1, 10, 0);

    public SpeederModule() {
        super("Speeder", "escape method", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if(Shadow.c.options.pickItemKey.isPressed()){
            ClientPlayerEntity player = Shadow.c.player;
            Shadow.c.player.setVelocity(Vec3d.ZERO);
            Vec3d forward = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
            for(int i = 0; i < slider.getThis(); i++){
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + forward.x * dist.getThis(), player.getY() + forward.y * dist.getThis(), player.getZ() + forward.z * dist.getThis(), true));
                player.updatePosition(player.getX() + forward.x * dist.getThis(), player.getY() + forward.y * dist.getThis(), player.getZ() + forward.z * dist.getThis());
            }
        }
    }

    @Override
    public void onRender() {

    }
}
