package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class RepairCmd extends Command {
    public RepairCmd() {
        super("repair", "repairs your held tool");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            ChatUtils.message("Please use the format >repair");
            return;
        }

        ClientPlayerEntity player = Shadow.c.player;

        if (!player.getAbilities().creativeMode) {
            ChatUtils.message("you must be in creative mode");
            return;
        }

        ItemStack stack = player.getInventory().getMainHandStack();
        if (stack.isEmpty()) {
            ChatUtils.message("please hold an item in your hand");
            return;
        }

        if (!stack.isDamageable()) {
            ChatUtils.message("this item cannot take damage");
            return;
        }

        if (!stack.isDamaged()) {
            ChatUtils.message("this item is not damaged");
            return;
        }
        stack.setDamage(0);
        Shadow.c.player.networkHandler
                .sendPacket(new CreativeInventoryActionC2SPacket(
                        36 + player.getInventory().selectedSlot, stack));

        ChatUtils.message("Item repaired.");
    }
}
