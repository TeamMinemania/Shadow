package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.FriendSystem;
import net.shadow.utils.ChatUtils;

public class FriendsCmd extends Command {
    public FriendsCmd() {
        super("friends", "exclude ppl");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"5"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Use >friends <add/list/remove/clear/op/unban> <name?>");
        }
        switch (args[0].toLowerCase()) {
            case "add":
                ChatUtils.message("Added " + args[1]);
                FriendSystem.friendsystem.add(args[1]);
                break;

            case "list":
                ChatUtils.message("Friends:");
                for (String fr : FriendSystem.friendsystem) {
                    ChatUtils.message(fr);
                }
                break;

            case "remove":
                ChatUtils.message("Removed " + args[1]);
                FriendSystem.friendsystem.remove(args[1]);
                break;

            case "clear":
                ChatUtils.message("Cleared Friends list!");
                FriendSystem.friendsystem.clear();
                break;

            case "op":
                for (String name : FriendSystem.friendsystem) {
                    Shadow.c.player.sendChatMessage("/op " + name);
                }
                break;

            case "unban":
                for (String name : FriendSystem.friendsystem) {
                    Shadow.c.player.sendChatMessage("/pardon " + name);
                }
                break;
        }
    }
}
