package net.shadow.prisma.packets.s2c;

import net.shadow.feature.module.OnlineServices;
import net.shadow.prisma.S2CPacket;

public class BroadcastPacket extends S2CPacket {
    public BroadcastPacket() {
        super("broadcast");
    }

    @Override
    public void on(Object[] args) {
        String message = (String) args[0];
        OnlineServices.message(message);
    }
}
