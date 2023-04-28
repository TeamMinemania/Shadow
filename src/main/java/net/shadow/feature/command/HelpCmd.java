package net.shadow.feature.command;

import net.shadow.feature.CommandRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class HelpCmd extends Command {
    public HelpCmd() {
        super("help", "shows all commands");
    }

    @Override
    public void call(String[] args) {
        StringBuilder nubkraft = new StringBuilder("Commands:\n");
        for (Command c : CommandRegistry.getList()) {
            nubkraft.append(c.getName()).append(" : ").append(c.getDesc()).append("\n");
        }
        ChatUtils.message(nubkraft.toString());
    }
}
