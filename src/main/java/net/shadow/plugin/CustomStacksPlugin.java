package net.shadow.plugin;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomStacksPlugin {
    private static final List<ItemStack> items = new ArrayList<>();

    public static void add(ItemStack stack) {
        items.add(stack);
    }

    public static List<ItemStack> getStacks() {
        return items;
    }

    public static void clear() {
        items.clear();
    }

    public static boolean has(ItemStack stack) {
        for (ItemStack i : items) {
            if (i.getItem().equals(stack.getItem()) && i.getNbt().equals(stack.getNbt())) {
                return true;
            }
        }
        return false;
    }
}
