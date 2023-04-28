package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

public class NoSrpModule extends Module implements PacketInput {
    public NoSrpModule() {
        super("NoSRP", "fake accepts resource packet", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof ResourcePackSendS2CPacket p) {
            ChatUtils.message("Fake accepted resource pack!");
            ChatUtils.message("URL:" + p.getURL());
            ChatUtils.message("REQUIRED: " + p.isRequired());
            event.cancel();
            Shadow.c.player.networkHandler.sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
        }
    }
}
