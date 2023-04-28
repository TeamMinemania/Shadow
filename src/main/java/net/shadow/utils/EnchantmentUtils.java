package net.shadow.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class EnchantmentUtils {
    //code from meteor client https://github.com/MeteorDevelopment/meteor-client (with minor modifications from me and update to 1.17 mappings)
    public static void addEnchantment(ItemStack itemStack, String enchId, int level) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        NbtList listTag;

        // Get list tag
        if (!tag.contains("Enchantments", 9)) {
            listTag = new NbtList();
            tag.put("Enchantments", listTag);
        } else {
            listTag = tag.getList("Enchantments", 10);
        }

        for (NbtElement _t : listTag) {
            NbtCompound t = (NbtCompound) _t;

            if (t.getString("id").equals(enchId)) {
                if (level == 256) {
                    t.putDouble("lvl", Double.NaN);
                } else {
                    t.putShort("lvl", (short) level);
                }
                return;
            }
        }

        // Add the enchantment if it doesn't already have it
        NbtCompound enchTag = new NbtCompound();
        enchTag.putString("id", enchId);
        enchTag.putShort("lvl", (short) level);

        listTag.add(enchTag);
    }
}
