package net.shadow.prisma.packets.s2c;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shadow.Shadow;
import net.shadow.prisma.S2CPacket;

public class IRCPacket extends S2CPacket {
    public IRCPacket() {
        super("chat");
    }

    @Override
    public void on(Object[] args) {
        String username = (String) args[0];
        String message = (String) args[1];
        if (Shadow.c.player != null)
            Shadow.c.player.sendMessage(Text.of(Formatting.GRAY + "[IRC] " + Formatting.WHITE + username + ": " + message), false);
    }
}
