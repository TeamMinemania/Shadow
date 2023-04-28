package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class CPermCmd extends Command {
    public CPermCmd() {
        super("cperm", "Set a client side op");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"4"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please provide an integer");
            return;
        }
        try {
            int uwu = Integer.parseInt(args[0]);
            Shadow.c.player.setClientPermissionLevel(uwu);
            ChatUtils.message("Set the Permission level to [" + uwu + "]");
        } catch (Exception e) {
            ChatUtils.message("Please provide an integer");
            e.printStackTrace();
        }

    }
}
