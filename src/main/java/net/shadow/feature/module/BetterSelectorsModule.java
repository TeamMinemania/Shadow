package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.ChatOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.plugin.GlobalConfig;

public class BetterSelectorsModule extends Module implements ChatOutput {
    public BetterSelectorsModule() {
        super("BetterSelectors", "let you use more selectors for entities", ModuleType.CHAT);
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
        String myname = Shadow.c.getSession().getProfile().getName();
        String parseo = "@a[name=!" + myname + "]";
        String parsec = "@a[name=!" + myname + ",limit=1,sort=nearest]";
        String message = event.getOriginalMessage().trim();
        if (!(message.startsWith("/") || message.startsWith(GlobalConfig.getPrefix())))
            return;

        message = message.replace("@o", parseo).replace("@f", "@a[distance=4..]").replace("@n", "@a[distance=..6]").replace("@c", parsec).replace("@ss", "§");
        ChatMessageC2SPacket packet = new ChatMessageC2SPacket(message);
        Shadow.c.player.networkHandler.sendPacket(packet);
        event.cancel();
    }
}
