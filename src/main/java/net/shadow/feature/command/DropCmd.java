package net.shadow.feature.command;

import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;

public class DropCmd extends Command {
    public DropCmd() {
        super("drop", "drop items");
    }

    public static void drop(int slot) {
        Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW, Shadow.c.player);
    }

    @Override
    public void call(String[] args) {
        for (int i = 9; i < 45; i++) {
            drop(i);
        }
    }
}
