package net.shadow.feature.command;

import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;

public class VoidCmd extends Command {
    public VoidCmd() {
        super("void", "void your inventory");
    }

    @Override
    public void call(String[] args) {
        for (int i = 9; i < 45; i++) {
            Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, i, 120, SlotActionType.SWAP, Shadow.c.player);
        }
    }
}
