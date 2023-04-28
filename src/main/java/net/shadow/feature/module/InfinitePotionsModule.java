package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class InfinitePotionsModule extends Module implements PacketOutput {
    public InfinitePotionsModule() {
        super("InfinitePotions", "make potions go brrt", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
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
        if (!isFrozen())
            return;

        if (event.getPacket() instanceof PlayerMoveC2SPacket)
            event.cancel();

    }

    public boolean isFrozen() {
        return isEnabled() && Shadow.c.player != null
                && !Shadow.c.player.getActiveStatusEffects().isEmpty()
                && Shadow.c.player.getVelocity().x == 0 && Shadow.c.player.getVelocity().z == 0;
    }
}
