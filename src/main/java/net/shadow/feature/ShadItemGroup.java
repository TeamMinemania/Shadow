/*
 * Copyright (c) Shadow client, Saturn5VFive, Wendellmeset and contributors 2023. All rights reserved.
 */

package net.shadow.feature;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.StringNbtReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShadItemGroup {
    final Text name;

    String nameS;

    ItemStack icon;

    List<ItemStack> items = new CopyOnWriteArrayList<>();
    
    public static ItemStack generateItemStackWithMeta(String nbt, Item item) {
        try {
            ItemStack stack = new ItemStack(item);
            stack.setNbt(StringNbtReader.parse(nbt));
            return stack;
        } catch (Exception ignored) {
            return new ItemStack(item);
        }
    }
    
    public ShadItemGroup(String name, ItemStack icon) {
        this.nameS = name;
        this.name = Text.of(name);
        this.icon = icon;
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        new ItemGroup(ItemGroup.GROUPS.length - 1, "shadow." + name) {
            @Override
            public ItemStack createIcon() {
                return icon;
            }

            @Override
            public void appendStacks(DefaultedList<ItemStack> stacks) {
                stacks.addAll(ShadItemGroup.this.getItems());
            }

            @Override
            public Text getDisplayName() {
                return ShadItemGroup.this.name;
            }
        };
    }

    public void addItem(ItemStack stack) {
        items.add(stack);
    }

    public void addItem(Item type, String nbt) {
        addItem(generateItemStackWithMeta(nbt, type));
    }
}
