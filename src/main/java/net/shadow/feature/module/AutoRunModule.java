package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.utils.ChatUtils;

public class AutoRunModule extends Module {
    static boolean wasoplasttick = false;
    final CustomValue<String> commands = this.config.create("Commands", "/say hacked; /say hacked");

    public AutoRunModule() {
        super("AutoRun", "auto run, separate two with ;", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (Shadow.c.player.hasPermissionLevel(4)) {
            ChatUtils.message("You were opped, running commands");
            String[] command = commands.getThis().split(";");
            for (String cmd : command) {
                Shadow.c.player.sendChatMessage(cmd);
            }
            this.setEnabled(false);
        }
    }

    @Override
    public void onRender() {

    }
}
