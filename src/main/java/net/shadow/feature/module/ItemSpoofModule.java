package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class ItemSpoofModule extends Module implements LeftClick, RightClick, PacketOutput {

    private boolean canSend = false;
    private boolean hasJustUpdated = false;
    private int tick = 0;

    public ItemSpoofModule() {
        super("ItemSpoof", "Conceal the item you are holding", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(RightClick.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(RightClick.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {
        tick++;
        if (hasJustUpdated && tick % 2 == 0) {
            canSend = true;
            Shadow.c.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(0));
            canSend = false;
            hasJustUpdated = false;
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket) {
            if (!canSend) {
                event.cancel();
            }
        }
    }

    @Override
    public void onRightClick(RightClickEvent event) {
        canSend = true;
        Shadow.c.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(Shadow.c.player.getInventory().selectedSlot));
        canSend = false;
        hasJustUpdated = true;
    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        canSend = true;
        Shadow.c.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(Shadow.c.player.getInventory().selectedSlot));
        canSend = false;
        hasJustUpdated = true;
    }
}
