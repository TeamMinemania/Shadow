package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.ChatOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.plugin.GlobalConfig;

public class MutebypassModule extends Module implements ChatOutput {
    public MutebypassModule() {
        super("MuteBypass", "bypass mute, working in hindi", ModuleType.CHAT);
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
        String message = event.getOriginalMessage().trim();
        if (message.startsWith("/"))
            return;

        if (message.startsWith(GlobalConfig.getPrefix()))
            return;
        ChatMessageC2SPacket packet = new ChatMessageC2SPacket("/minecraft:me " + message);
        Shadow.c.getNetworkHandler().sendPacket(packet);
        event.cancel();
    }
}
