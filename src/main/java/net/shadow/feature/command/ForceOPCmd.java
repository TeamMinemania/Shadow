package net.shadow.feature.command;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.util.Session;
import net.minecraft.world.GameMode;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.GlobalConfig;
import net.shadow.utils.ChatUtils;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class ForceOPCmd extends Command {
    public ForceOPCmd() {
        super("forceop", "get op instantly on cracked servers");
    }

    private static void authUsername(String username) {
        Session ns = new Session(username, UUID.randomUUID().toString(), "memedonlolez", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
        Shadow.c.getSessionProperties().clear();
        ((net.shadow.mixin.MinecraftClientAccessor) Shadow.c).setSession(ns);
    }

    private String getPlayer() {
        for (PlayerListEntry player : Shadow.c.getNetworkHandler().getPlayerList()) {
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) {
                return player.getProfile().getName();
            }
        }
        return "None";
    }

    @Override
    public void call(String[] args) {
        InetSocketAddress socket = (InetSocketAddress) Shadow.c.player.networkHandler.getConnection().getAddress();
        GlobalConfig.serverAddress = new ServerAddress(socket.getHostName(), socket.getPort());
        String nick;
        if (args.length == 0) {
            nick = getPlayer();
            if (nick.equals("None")) {
                ChatUtils.message("Could not find a suitable OP Player, use arguments to define");
                return;
            }
        } else {
            nick = args[0];
        }
        authUsername(nick);
        GlobalConfig.reconnectInstantly = true;
        Shadow.c.world.disconnect();
    }
}
