package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.GameMode;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;

public class GamemodeCmd extends Command {
    public GamemodeCmd() {
        super("gamemode", "changes your gamemode client side");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"survival", "creative"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        ClientPlayerEntity player = Shadow.c.player;

        switch (String.join(" ", args).toLowerCase()) {
            case "survival" -> {
                NotificationSystem.notifications.add(new Notification("Gamemode", "Set players gamemode to survival", 150));
                Shadow.c.interactionManager.setGameMode(GameMode.SURVIVAL);
            }
            case "creative" -> {
                NotificationSystem.notifications.add(new Notification("Gamemode", "Set players gamemode to creative", 150));
                Shadow.c.interactionManager.setGameMode(GameMode.CREATIVE);
            }
            case "c" -> {
                NotificationSystem.notifications.add(new Notification("Gamemode", "Set players gamemode to creative", 150));
                Shadow.c.interactionManager.setGameMode(GameMode.CREATIVE);
            }
            case "s" -> {
                NotificationSystem.notifications.add(new Notification("Gamemode", "Set players gamemode to survival", 150));
                Shadow.c.interactionManager.setGameMode(GameMode.SURVIVAL);
            }
            default -> {
                ChatUtils.message("Please use the format >gamemode <creative/c/survival/s>");
                return;
            }
        }
    }
}