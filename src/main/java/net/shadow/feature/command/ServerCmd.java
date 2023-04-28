package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class ServerCmd extends Command {
    public ServerCmd() {
        super("server", "get information about the server");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"ip", "position"});
        }
        if(index == 1){
            return List.of(new String[]{"copy"});
        }
        return List.of(new String[0]);
    }

    private static String getVal(String s) {
        return switch (s.toLowerCase()) {
            case "ip" -> Shadow.c.getNetworkHandler().getConnection().getAddress().toString();
            case "position" -> Shadow.c.player.getX() + " " + Shadow.c.player.getY() + " " + Shadow.c.player.getZ();
            default -> s;
        };
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please use the format >server <ip/position> <?copy>");
            return;
        }

        String th = getVal(args[0]);

        if (args.length == 2) {
            if (!args[1].equals("copy")) return;
            Shadow.c.keyboard.setClipboard(th);
        } else {
            if (args[0].equalsIgnoreCase("ip")) {
                ChatUtils.message("IP: " + th);
            } else {
                ChatUtils.message("Position: " + th);
            }
        }
    }
}
