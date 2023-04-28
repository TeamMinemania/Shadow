package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class SayCmd extends Command {
    public SayCmd() {
        super("say", "say stuff that starts with the prefix");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please use the format >say <text>");
            return;
        }

        String m = String.join(" ", args);
        Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket(m));
    }
}
