package net.shadow.feature.module.other;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

public class Unload extends Module {
    public static boolean loaded = true;

    public Unload() {
        super("Unload", "unloads the client", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        loaded = false;
        ChatUtils.message("Client Unloaded, press F3+D to clear your chat and hide this message!");
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
