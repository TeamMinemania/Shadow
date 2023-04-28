package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;

public class BackdoorPrefixCmd extends Command {
    public BackdoorPrefixCmd() {
        super("bd", "add the backdoor prefix");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            String command = ModuleRegistry.find("Moldoor").config.get("Prefix").getThis() + String.join(" ", args);
            Shadow.c.player.sendChatMessage(command);
        }
    }
}
