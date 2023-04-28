package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class GetIpCmd extends Command {
    public GetIpCmd() {
        super("getaddress", "get servers real ip");
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message(Shadow.c.player.networkHandler.getConnection().getAddress().toString());
        Shadow.c.keyboard.getClipboard();
    }
}