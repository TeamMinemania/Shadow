package net.shadow.feature.module;

import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AntiBanModule extends Module implements PacketInput {
    public AntiBanModule() {
        super("AntiBan", "stay in the server after ban", ModuleType.EXPLOIT);
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
        if (event.getPacket() instanceof DisconnectS2CPacket) {
            event.cancel();
        }
        if (event.getPacket() instanceof WorldEventS2CPacket) {
            event.cancel();
        }
    }
}
