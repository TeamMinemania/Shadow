package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.EnchantmentUtils;

public class EnchantCmd extends Command {
    public EnchantCmd() {
        super("enchant", "enchant items with enchantments");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return Registry.ENCHANTMENT.getIds().stream().map(id -> id.getPath()).toList();
        }
        if(index == 1){
            return List.of(new String[]{"5"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        try {
            ItemStack stack = Shadow.c.player.getInventory().getMainHandStack();
            if (!stack.isEmpty() && args.length == 2) {
                String enchid = args[0];
                int level = Integer.parseInt(args[1]);
                EnchantmentUtils.addEnchantment(stack, enchid, level);
                NotificationSystem.notifications.add(new Notification("Enchant", "Enchanted Item", 150));
            }
            if (stack.isEmpty()) {
                ChatUtils.message("Please hold an item in your hand");
            }
            if (args.length != 2) {
                ChatUtils.message("Please use the format >enchant <enchantment> <level>");
            }
        } catch (Exception e) {
            ChatUtils.message("Incorrect Enchantments!");
        }
    }
}
