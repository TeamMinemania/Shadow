package net.shadow.feature.command;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import net.shadow.mixin.IdentifierAccessor;
import net.shadow.mixin.IdentifierMixin;

import java.util.List;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class StuckShulkerCmd extends Command {
    public StuckShulkerCmd() {
        super("test2", "the test packets");
    }

    @Override
    public void call(String[] args) {
    }
}
