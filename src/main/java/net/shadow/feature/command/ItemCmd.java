package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.ItemsPlugin;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;

public class ItemCmd extends Command {
    public ItemCmd() {
        super("item", "item storage shit");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"list", "save", "remove"});
        }
        if(index == 1){
            return List.of(new String[]{"myitemname"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Incorrect arguments, use >item [list/save/remove]");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                ChatUtils.message("Items:");
                for (String key : ItemsPlugin.itemlist.keySet()) {
                    ChatUtils.message(key);
                }
            }
            case "save" -> {
                if (args.length < 2) {
                    ChatUtils.message("Incorrect arguments, use >item save [name]");
                    return;
                }
                ItemsPlugin.add(Shadow.c.player.getMainHandStack(), args[1]);
                NotificationSystem.notifications.add(new Notification("Save Item", "Saved " + args[1], 150));
            }
            case "remove" -> {
                if (args.length < 2) {
                    ChatUtils.message("Incorrect arguments, use >item remove [name]");
                    return;
                }
                ItemsPlugin.remove(args[1]);
                NotificationSystem.notifications.add(new Notification("Remove Item", "Removed " + args[1], 150));
            }
        }
    }
}
