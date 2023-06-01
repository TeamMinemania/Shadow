package net.shadow.feature.module;


import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;

public class DiscordRPCModule extends Module {
    private static final DiscordRichPresence rc = new DiscordRichPresence();
    private static final DiscordRPC is = DiscordRPC.INSTANCE;
    final CustomValue<String> details = this.config.create("Details", "For Minecraft 1.19.2");
    private int t;

    public DiscordRPCModule() {
        super("DiscordRPC", "shows shadow client usage", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        is.Discord_Initialize("1023356463080554600", handlers, true, null);
        updateDetails();
        rc.startTimestamp = System.currentTimeMillis() / 1000L;
        is.Discord_UpdatePresence(rc);
    }

    @Override
    public void onDisable() {
        is.Discord_Shutdown();
    }

    @Override
    public void onUpdate() {
        if (t == 80) {
            updateDetails();
            t = 0;
        }
        t++;
    }

    @Override
    public void onRender() {

    }

    private void updateDetails() {
        rc.state = "Shadow v0.8.2";
        rc.details = details.getThis();
        rc.largeImageKey = "shad_icon";
        rc.largeImageText = "Shadow Client";
        is.Discord_UpdatePresence(rc);
    }
}
