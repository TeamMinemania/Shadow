package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.shadow.feature.base.Command;
import java.util.List;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.shadow.utils.ChatUtils;

public class TestCmd extends Command {
    public TestCmd() {
        super("lol", "real untested");
    }

    @Override
    public void call(String[] args) {
        
    }
}
