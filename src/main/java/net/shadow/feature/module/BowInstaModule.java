package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class BowInstaModule extends Module implements PacketOutput {
    final SliderValue power = this.config.create("Power", 1, 1, 20, 0);
    final SliderValue spoofs = this.config.create("Spoofs", 1, 1, 400, 0);
    final BooleanValue sfx = this.config.create("Sfx", false);
    final MultiValue mode = this.config.create("Mode", "New", "New", "Old");

    public BowInstaModule() {
        super("BowInsta", "kill insta bow", ModuleType.COMBAT);
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

        if (event.getPacket() instanceof PlayerActionC2SPacket packet) {
            ClientPlayerEntity player = Shadow.c.player;
            if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM && player.getMainHandStack().getItem() == Items.BOW) {
                if (mode.getThis().equalsIgnoreCase("old")) {
                    old();
                } else {
                    nmethod();
                }
            }
        }
    }

    private void old() {
        ClientPlayerEntity player = Shadow.c.player;
        double yaw = Math.toRadians(player.getYaw());
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX() - Math.sin(yaw) * power.getThis(), player.getY() + 5, player.getZ() + Math.cos(yaw) * power.getThis(), player.getYaw(), player.getPitch(), false));
    }

    private void nmethod() {
        ClientPlayerEntity player = Shadow.c.player;
        if (sfx.getThis()) {
            Shadow.c.player.playSound(Shadow.OOF, 1f, 1f);
        }
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        for (int i = 0; i < spoofs.getThis(); i++) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() - 1.0E-10, player.getZ(), true));
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + 1.0E-10, player.getZ(), false));
        }
    }
}
