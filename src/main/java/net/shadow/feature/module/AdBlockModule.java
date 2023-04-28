package net.shadow.feature.module;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class AdBlockModule extends Module implements PacketInput {

    BooleanValue unfilter = this.config.create("Unblock Griefable", false);

    public AdBlockModule() {
        super("AdBlock", "Imagine AdBlockPlus but for your minehut chat", ModuleType.CHAT);
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
        if (event.getPacket() instanceof GameMessageS2CPacket packet) {
            String message = packet.getMessage().getString();
            System.out.println(message);
            if (message.contains("[AD]")) {
                event.cancel();
            }
        }
    }
}
