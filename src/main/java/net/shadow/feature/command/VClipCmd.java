package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class VClipCmd extends Command {
    public VClipCmd() {
        super("vclip", "teleports the player vertically");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            ChatUtils.message("Please use the format >vclip <amount>");
            return;
        }

        try {
            ClientPlayerEntity player = Shadow.c.player;
            player.updatePosition(player.getX(),
                    player.getY() + Double.parseDouble(args[0]), player.getZ());
        } catch (NumberFormatException e) {
            ChatUtils.message("Please use the format >vclip <amount>");
        }
    }
}
