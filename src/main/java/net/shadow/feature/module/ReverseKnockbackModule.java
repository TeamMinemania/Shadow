package net.shadow.feature.module;

import org.apache.commons.codec.binary.StringUtils;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class ReverseKnockbackModule extends Module implements PacketOutput{

    public ReverseKnockbackModule() {
        super("ReverseKnockback", "knockback but like backwar", ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
    
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (!this.isEnabled()) return;
        MinecraftClient client = Shadow.c;
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            event.cancel();
            double x = packet.getX(0);
            double y = packet.getY(0);
            double z = packet.getZ(0);

            PlayerMoveC2SPacket newPacket;
            if (packet instanceof PlayerMoveC2SPacket.Full) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, MathHelper.wrapDegrees(client.player.getYaw() + 180), 0, packet.isOnGround());
            } else {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround(MathHelper.wrapDegrees(client.player.getYaw() + 180), 0, packet.isOnGround());
            }
            client.player.networkHandler.getConnection().send(newPacket);
        }
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }
}
