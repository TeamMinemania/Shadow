package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.*;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class InvStealerModule extends Module implements PacketOutput {
    public InvStealerModule() {
        super("InvStealer", "Take items out of inventories client side", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket || event.getPacket() instanceof KeepAliveC2SPacket || event.getPacket() instanceof PlayerInteractItemC2SPacket || event.getPacket() instanceof PlayerInteractBlockC2SPacket || event.getPacket() instanceof PlayerInteractEntityC2SPacket || event.getPacket() instanceof CreativeInventoryActionC2SPacket)
            return;

        event.cancel();

    }
}
