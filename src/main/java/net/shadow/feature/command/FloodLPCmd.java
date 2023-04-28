package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Random;

public class FloodLPCmd extends Command {
    public FloodLPCmd() {
        super("floodlp", "make a bunch of lp groups");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"50"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("use >floodlp <amount>");
            return;
        }
        int pp = 0;
        try {
            pp = Integer.parseInt(args[0]);
        } catch (Exception e) {
            ChatUtils.message("use >floodlp <amount>");
        }
        Random r = new Random();
        for (int i = 0; i < pp; i++) {
            Shadow.c.player.sendChatMessage("/lp creategroup " + i + r.nextInt(100));
        }
    }
}
