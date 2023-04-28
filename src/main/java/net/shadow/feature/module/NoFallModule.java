package net.shadow.feature.module;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.mixin.PlayerMovePacketMixin;

public class NoFallModule extends Module implements PacketOutput {

    final BooleanValue compatable = this.config.create("Old", false);

    public NoFallModule() {
        super("NoFall", "no fall damge", ModuleType.MOVEMENT);
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
        if (compatable.getThis()) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX(), Shadow.c.player.getY(), Shadow.c.player.getZ(), true));
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (compatable.getThis()) return;

        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            ((PlayerMovePacketMixin)packet).setOnGround(true);
        }
    }
}
