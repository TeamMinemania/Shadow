package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;

public class BypassShulkerCmd extends Command {
    public BypassShulkerCmd() {
        super("bypassshulker", "Bypasses shulker restrictions");
    }

    @Override
    public void call(String[] args) {
        for (int i = 0; i < 100; i++) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        }
    }
}
