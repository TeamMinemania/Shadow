package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.event.events.ChatOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.plugin.GlobalConfig;

public class ChatFilterModule extends Module implements ChatOutput {

    public ChatFilterModule() {
        super("FilterBypass", "chat filter is gone", ModuleType.CHAT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(ChatOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(ChatOutput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentMessage(ChatOutputEvent event) {
        String message = event.getOriginalMessage();
        if (message.startsWith("/") || message.startsWith(GlobalConfig.getPrefix())) {
            return;
        }
        StringBuilder rvmsg = new StringBuilder(message).reverse();
        String msgout = rvmsg.toString();
        String msgformatted = "\u202E " + rvmsg;
        event.setMessage(msgformatted);
    }
}
