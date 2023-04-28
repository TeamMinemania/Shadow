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
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.shadow.utils.ChatUtils;

public class gwatCmd extends Command {
    public gwatCmd() {
        super("gwat", "get access token");
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message("Copied Login Details to clipboard!");
        Shadow.c.keyboard.setClipboard(Shadow.c.getSession().getAccessToken() + " " + Shadow.c.getSession().getUuid() + " " + Shadow.c.getSession().getUsername());
    }
}
