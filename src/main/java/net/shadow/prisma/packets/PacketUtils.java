package net.shadow.prisma.packets;

import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.OnlineServices;
import net.shadow.prisma.APISocket;

public class PacketUtils {
    public static void sendSafely(String name, Object... args) {
        if(Shadow.prismaSocket != null)
            Shadow.prismaSocket.s.emit(name,args);
    }
    public static APISocket getSafely() {
        if(Shadow.prismaSocket == null)
            throw new RuntimeException("Prisma is disabled!");
        return Shadow.prismaSocket;
    }
    public static boolean isMoletunnelEnabled() {
        return ((OnlineServices) ModuleRegistry.getByClass(OnlineServices.class)).moleTunnel.getThis();
    }
}
