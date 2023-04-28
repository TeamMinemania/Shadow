package net.shadow.feature.module.movement;

import org.apache.commons.codec.binary.StringUtils;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.utils.ChatUtils;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.mixin.VehicleMovePacketMixin;

public class BotMovement extends Module implements PacketOutput{

    public BotMovement() {
        super("RoboWalk", "BotMovement for liveoverflow server", ModuleType.MOVEMENT);
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

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            if(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround || packet instanceof PlayerMoveC2SPacket.Full){
                double x = Math.round(packet.getX(0) * 100.0) / 100.0; //round packets as best we can
                double z = Math.round(packet.getZ(0) * 100.0) / 100.0;
                long dx = ((long)(x * 1000)) % 10; //simulate the check that liveoverflow runs 
                long dz = ((long)(z * 1000)) % 10;
                if(dx != 0 || dz != 0){
                    event.cancel();
                    return;
                }

                ((PlayerMovePacketMixin) packet).setX(x);
                ((PlayerMovePacketMixin) packet).setZ(z);
            }   
        }
        if(event.getPacket() instanceof VehicleMoveC2SPacket packet){
            double x = Math.round(packet.getX() * 100.0) / 100.0; //round packets as best we can
            double z = Math.round(packet.getZ() * 100.0) / 100.0;
            long dx = ((long)(x * 1000)) % 10; //simulate the check that liveoverflow runs 
            long dz = ((long)(z * 1000)) % 10;
            if(dx != 0 || dz != 0){
                event.cancel();
                return;
            }

            ((VehicleMovePacketMixin) packet).setX(x);
            ((VehicleMovePacketMixin) packet).setZ(z);

        }
    }
}
