package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class EquipCmd extends Command {
    public EquipCmd() {
        super("equip", "equip any item on your body slots");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"head", "chest", "legs", "feet"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        ItemStack stack = Shadow.c.player.getInventory().getMainHandStack();
        if (args.length != 1) {
            ChatUtils.message("You must provide one slot head/chest/legs/feet");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "head" -> {
                //39 HEAD - 36 FEETw
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, 36 + Shadow.c.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, Shadow.c.player);
                ChatUtils.message("equipped item on head");
            }
            case "chest" -> {
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, 36 + Shadow.c.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, Shadow.c.player);
                ChatUtils.message("equipped item on chest");
            }
            case "legs" -> {
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, 36 + Shadow.c.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, Shadow.c.player);
                ChatUtils.message("equipped item on legs");
            }
            case "feet" -> {
                Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, 36 + Shadow.c.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, Shadow.c.player);
                ChatUtils.message("equipped item on feet");
            }
            default -> ChatUtils.message("Incorrect slot, slots are chest, legs, feet, and head");
        }
    }
}
