package net.shadow.feature.command;

import net.minecraft.client.network.PlayerListEntry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class GetCmd extends Command {
    public GetCmd() {
        super("massdo", "do a command for every player, just put @a where you want the player name");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("You must provide a message!");
            return;
        }

        new Thread(() -> {
            for (PlayerListEntry info : Shadow.c.player.networkHandler.getPlayerList()) {
                String thisfucker = info.getProfile().getName();
                String after = String.join(" ", args).replace("@a", thisfucker);
                Shadow.c.player.sendChatMessage(after);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}