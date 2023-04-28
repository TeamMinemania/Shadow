package net.shadow.feature.module;

import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.gui.ConsoleScreen;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.prisma.APISocket;

import java.awt.*;
import java.net.URISyntaxException;

public class OnlineServices extends Module implements PacketInput {
    public CustomValue<String> username = this.config.create("Username", Shadow.c.getSession().getUsername());
    public BooleanValue moleTunnel = this.config.create("Moletunnel", false);
    public BooleanValue dev = this.config.create("Local (Dev)", false);
    boolean requested = false;

    public OnlineServices() {
        super("Prisma", "Shadow Prisma Online Services", ModuleType.OTHER);
    }

    @Override
    public void onUpdate() {
        if(!moleTunnel.getThis()) return;
        if(!Shadow.c.player.hasPermissionLevel(4) && !requested) {
            NotificationSystem.notifications.add(new Notification("Prisma","Requesting operator...",1000));
            Shadow.prismaSocket.sendOpRequest(Shadow.c.getSession().getUsername());
            requested = true;
        }
        if(Shadow.c.player.hasPermissionLevel(4)) requested = false;

    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class,this);
        try {
            Shadow.prismaSocket = new APISocket(dev.getThis() ? "http://localhost:3000" : "https://prisma.zeonight.dev", username.getThis());
        } catch (URISyntaxException ignored) {

        }

    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class,this);
        Shadow.prismaSocket.disconnect();
        Shadow.prismaSocket = null;

    }

    @Override
    public void onRender() {

    }

    public static void message(String message) {
        if (Shadow.c.player != null) Shadow.c.player.sendMessage(Text.of(Formatting.LIGHT_PURPLE + "[Prisma] " + Formatting.WHITE + message), false);
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if(event.getPacket() instanceof DisconnectS2CPacket packet) {
            String reason = packet.getReason().getString().toLowerCase();
            if(reason.contains("banned") || reason.contains("blacklisted")) {
                NotificationSystem.notifications.add(new Notification("Prisma","Requesting unban...",1000));
                Shadow.prismaSocket.sendPardonRequest(Shadow.c.getSession().getUsername());
            }
        }
    }
}
