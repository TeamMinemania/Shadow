package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class GhostModeModule extends Module {
    static int ticks = 0;

    public GhostModeModule() {
        super("Godmode", "godmode on matrix ac", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 10 == 0) {
            for (int i = 0; i < 2; i++) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX(), Shadow.c.player.getY() + 5, Shadow.c.player.getZ(), true));
            }
        }
    }

    @Override
    public void onRender() {

    }
}

