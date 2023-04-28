package net.shadow.feature.command;

import net.minecraft.client.util.Session;
import net.shadow.mixin.MinecraftClientAccessor;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Command;
import java.util.List;
import java.util.Optional;

import me.x150.authlib.login.mojang.MinecraftAuthenticator;
import me.x150.authlib.login.mojang.profile.MinecraftProfile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.shadow.utils.ChatUtils;

public class LwatCmd extends Command {
    public LwatCmd() {
        super("lwat", "login with access token");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"Token"});
        }
        if(index == 1){
            return List.of(new String[]{"UUID (With dashes)"});
        }
        if(index == 2){
            return List.of(new String[]{"Username"});
        }

        return List.of(new String[0]);
    }


    @Override
    public void call(String[] args) {
        if(args.length == 1){
            MinecraftAuthenticator real = new MinecraftAuthenticator();
            MinecraftProfile target = real.getGameProfile(args[0]);
            Session newSession = new Session(target.getUuid().toString(), target.getUsername(), args[0], Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
            ((MinecraftClientAccessor) Shadow.c).setSession(newSession);
            ChatUtils.message("Updated game profile information");
        }else if(args.length == 3){
            Session newSession = new Session(args[2], args[1], args[0], Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
            ((MinecraftClientAccessor) Shadow.c).setSession(newSession);
            ChatUtils.message("Updated game profile information");
        }else{
            ChatUtils.message("Invalid information");
        }

    }
}
