package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class AutoLeaveModule extends Module {

    final SliderValue s = this.config.create("Min Health", 7, 1, 20, 1);
    final MultiValue m = this.config.create("Mode", "Quit", "Quit", "Chars", "Teleport");

    public AutoLeaveModule() {
        super("AutoLeave", "Automatically leaves the server", ModuleType.COMBAT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (Shadow.c.player.getAbilities().creativeMode)
            return;

        if (Shadow.c.isInSingleplayer() && Shadow.c.player.networkHandler.getPlayerList().size() == 1)
            return;

        if (Shadow.c.player.getHealth() > s.getThis() * 2F)
            return;

        switch (m.getThis().toLowerCase()) {
            case "quit" -> Shadow.c.world.disconnect();
            case "chars" -> Shadow.c.player.networkHandler
                    .sendPacket(new ChatMessageC2SPacket("\u00a7"));
            case "teleport" -> Shadow.c.player.networkHandler.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(3.1e7, 100, 3.1e7, false));
        }

        // disable
        setEnabled(false);

    }

    @Override
    public void onRender() {

    }
}
