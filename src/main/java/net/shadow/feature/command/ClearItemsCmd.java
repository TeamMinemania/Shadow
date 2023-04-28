package net.shadow.feature.command;

import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.CustomStacksPlugin;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class ClearItemsCmd extends Command {
    public ClearItemsCmd() {
        super("clearitems", "clear all items in the logger");
    }

    @Override
    public void call(String[] args) {
        CustomStacksPlugin.clear();
        Notification n = new Notification("Logger", "cleared logger", 150);
        NotificationSystem.notifications.add(n);
    }
}
