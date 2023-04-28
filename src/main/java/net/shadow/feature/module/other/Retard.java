package net.shadow.feature.module.other;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

import java.util.Random;

public class Retard extends Module {
    boolean wasSneaking = false;

    public Retard() {
        super("Retard", "makes you act all retarted", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Random rand = new Random();
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((rand.nextInt(360) - 180), (rand.nextInt(180) - 90), Shadow.c.player.isOnGround()));
        Shadow.c.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        if (wasSneaking) {
            wasSneaking = false;
            Shadow.c.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Shadow.c.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        } else {
            wasSneaking = true;
            Shadow.c.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Shadow.c.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }

    }

    @Override
    public void onRender() {

    }
}
