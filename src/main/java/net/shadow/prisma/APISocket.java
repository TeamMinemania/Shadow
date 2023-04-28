package net.shadow.prisma;

import io.socket.client.IO;
import io.socket.client.Socket;
import net.shadow.Shadow;
import net.shadow.prisma.packets.PacketUtils;
import net.shadow.prisma.packets.s2c.BroadcastPacket;
import net.shadow.prisma.packets.s2c.IRCPacket;
import net.shadow.prisma.packets.s2c.moletunnel.OpPacket;
import net.shadow.prisma.packets.s2c.moletunnel.PardonPacket;

import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class APISocket {
    private String username;
    private final ArrayList<S2CPacket> s2CPackets;
    public Socket s;

    public APISocket(String url, String username) throws URISyntaxException {
        this.username = username;
        this.s2CPackets = new ArrayList<>();
        s = IO.socket(url);
        connect();
        startListening();
    }

    private void startListening() {
        registerS2C(IRCPacket.class);
        registerS2C(BroadcastPacket.class);
        registerS2C(OpPacket.class);
        registerS2C(PardonPacket.class);
    }

    private void connect() {
        s.connect();
        System.out.println("Emmiting launch event");
        s.emit("launch", username, Shadow.c.getGameVersion());

    }

    public void disconnect() {
        s.disconnect();
        s.off();
    }
    private void registerS2C(Class<? extends S2CPacket> clazz) {
        S2CPacket instance = null;
        for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
            if (declaredConstructor.getParameterCount() != 0) {
                throw new IllegalArgumentException(clazz.getName() + " has invalid constructor: expected " + clazz.getName() + "(), got " + declaredConstructor);
            }
            try {
                instance = (S2CPacket) declaredConstructor.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to make instance of " + clazz.getName(), e);
            }
       }
        S2CPacket finalInstance = instance;
        s.on(instance.getPacketName(),(args) -> {
            if(finalInstance.isMoletunnelOnly() && !PacketUtils.isMoletunnelEnabled()) return;
            finalInstance.on(args);
        });
        s2CPackets.add(instance);
    }

    public void sendMessage(String message) {
        s.emit("chat", message);
    }

    public void sendOpRequest(String username) {
        s.emit("perms", username, Shadow.c.player.networkHandler.getConnection().getAddress().toString());
    }
    public void sendPardonRequest(String username) {
        s.emit("pardon", username, Shadow.c.player.networkHandler.getConnection().getAddress().toString());
    }
    public void sendPardonRequest(String username,String serverAddress) {
        s.emit("pardon", username, Shadow.c.player.networkHandler.getConnection().getAddress().toString());
    }
}
