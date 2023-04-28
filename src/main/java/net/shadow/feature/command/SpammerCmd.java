package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Arrays;

public class SpammerCmd extends Command {
    public SpammerCmd() {
        super("spam", "Instant spam");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"5"});
        }
        if(index == 1){
            return List.of(new String[]{"Shadow client on top!"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 2) {
            ChatUtils.message("Please use the format >spam <times> <message>");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        try {
            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                Shadow.c.player.sendChatMessage(message);
            }
        } catch (NumberFormatException e) {
            ChatUtils.message("Incorrect format, please use >spam <times> <message>");
        }
    }
}
