package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class HatClickModule extends Module implements PacketOutput {
    public HatClickModule() {
        super("ArmorClick", "click items onto your head", ModuleType.EXPLOIT);
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
        if (event.getPacket() instanceof ClickSlotC2SPacket packet) {
            if (packet.getActionType() == SlotActionType.PICKUP && packet.getButton() == 0 && packet.getSlot() >= 5 && packet.getSlot() <= 8) {
                int slot = packet.getSlot() - 5;
                int saveslot = getSaveSlot();
                event.cancel();
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, saveslot, 0, SlotActionType.PICKUP, Shadow.c.player);
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, saveslot, 39 - slot, SlotActionType.SWAP, Shadow.c.player);
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, saveslot, 0, SlotActionType.PICKUP, Shadow.c.player);
            }
        }
    }

    private int getSaveSlot() {
        for (int i = 0; i < 9; i++) {
            if (!Shadow.c.player.getInventory().getStack(i).isEmpty())
                continue;

            return i + 36;
        }
        return 36;
    }
}
