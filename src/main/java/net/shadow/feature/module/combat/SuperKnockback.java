package net.shadow.feature.module.combat;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class SuperKnockback extends Module implements PacketOutput {
    final MultiValue mode = this.config.create("Mode", "Strong", "Strong", "Inverse");
    SliderValue spoofs = this.config.create("Spoofs", 1, 1, 200, 0);

    public SuperKnockback() {
        super("Knockback", "add little of kb", ModuleType.COMBAT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "[" + this.mode.getThis() + "]";
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
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            nmethod();
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (!mode.getThis().equals("Inverse")) return;
            if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
                return;
            event.cancel();
            double x = packet.getX(0);
            double y = packet.getY(0);
            double z = packet.getZ(0);

            Packet<?> newPacket;
            if (packet instanceof PlayerMoveC2SPacket.Full) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, MathHelper.wrapDegrees(Shadow.c.player.getYaw() + 180), 0, packet.isOnGround());
            } else {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround(MathHelper.wrapDegrees(Shadow.c.player.getYaw() + 180), 0, packet.isOnGround());
            }

            Shadow.c.player.networkHandler.getConnection().send(newPacket);
        }
    }

    private void nmethod() {
        ClientPlayerEntity player = Shadow.c.player;
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        //??????????????
    }
}
