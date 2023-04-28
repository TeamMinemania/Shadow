package net.shadow.feature.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

import java.net.InetSocketAddress;
import java.util.UUID;

public class SocketKickCmd extends Command {
    public SocketKickCmd() {
        super("socketkick", "kick players off of a cracked server");
    }


    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        new Thread(() -> {
            String player = PlayerUtils.completeName(args[0]);
            InetSocketAddress socket = (InetSocketAddress) Shadow.c.player.networkHandler.getConnection().getAddress();
            ClientConnection connection = ClientConnection.connect(socket, true);
            connection.setPacketListener(new ClientLoginPacketListener() {

                @Override
                public void onDisconnected(Text var1) {

                }

                @Override
                public ClientConnection getConnection() {
                    return null;
                }

                @Override
                public void onHello(LoginHelloS2CPacket var1) {
                    connection.disconnect(Text.of("shadow client on top"));

                }

                @Override
                public void onSuccess(LoginSuccessS2CPacket var1) {

                }

                @Override
                public void onDisconnect(LoginDisconnectS2CPacket var1) {

                }

                @Override
                public void onCompression(LoginCompressionS2CPacket var1) {

                }

                @Override
                public void onQueryRequest(LoginQueryRequestS2CPacket var1) {

                }

            });
            ChatUtils.message("Kicking " + player);
            connection.send(new HandshakeC2SPacket(socket.getHostName(), socket.getPort(), NetworkState.LOGIN));
            connection.send(new LoginHelloC2SPacket(new GameProfile(UUID.fromString("91c71ac4-73a9-4659-a550-1a650731836e"), player)));
        }).start();
    }
}
