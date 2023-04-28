package net.shadow.feature.module;

import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

public class DebuggerModule extends Module implements PacketInput {
    public DebuggerModule() {
        super("Debugger", "see more information about the world around you", ModuleType.OTHER);
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
        if (event.getPacket() instanceof GameStateChangeS2CPacket packet) {
            ChatUtils.message("GameStateChange: " + packet.getReason().toString());
        }
    }
}
