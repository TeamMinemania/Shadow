package net.shadow.feature;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.plugin.CustomStacksPlugin;
import net.shadow.plugin.GlobalConfig;
import net.shadow.plugin.ItemsPlugin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static net.shadow.Shadow.loadString;

public class ItemRegistry {

    public static void register() {
        // Command Items


        FabricItemGroupBuilder.create(
                        new Identifier("shadow", "command"))
                .icon(() -> new ItemStack(Items.COMMAND_BLOCK))
                .appendItems(stacks -> {
                    stacks.add(new ItemStack(Blocks.COMMAND_BLOCK, 1));
                    stacks.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK, 1));
                    stacks.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK, 1));
                    stacks.add(new ItemStack(Items.COMMAND_BLOCK_MINECART, 1));
                    stacks.add(new ItemStack(Blocks.BARRIER, 1));
                    stacks.add(new ItemStack(Blocks.STRUCTURE_BLOCK, 1));
                    stacks.add(new ItemStack(Blocks.STRUCTURE_VOID, 1));
                    stacks.add(new ItemStack(Blocks.JIGSAW, 1));
                    stacks.add(new ItemStack(Blocks.SPAWNER, 1));
                })
                .build();
        }
    
// Read the entries from the text file
try (InputStream is = ItemRegistry.class.getClassLoader().getResourceAsStream("itemRegistry.txt");
     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
    String line;
    while ((line = reader.readLine()) != null) {
        // Parse the item data
        String[] parts = line.split(";");
        String itemId = parts[1];
        String nbtBase64 = parts[2];
        CompoundTag nbt = (CompoundTag) NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(nbtBase64)));

        // Create an instance of the item
        Item item = Registry.ITEM.get(new Identifier(itemId));
        if (item == null) {
            throw new RuntimeException("Unknown item ID: " + itemId);
        }
        ItemStack stack = new ItemStack(item);
        stack.setTag(nbt);

        // Add the item to the appropriate item group
        DefaultedList<ItemStack> stacks = new DefaultedList<>(ItemStack.EMPTY);
        stacks.add(stack);
        ItemGroup itemGroup;
        if (parts[0].equals("special")) {
            itemGroup = new ItemGroup(ItemGroup.GROUPS.length, "shadow.special") {
                @Override
                public ItemStack createIcon() {
                    return new ItemStack(Items.STRUCTURE_VOID);
                }

                @Override
                public void appendStacks(DefaultedList<ItemStack> stacks) {
                    stacks.addAll(stacks);
                }

                @Override
                public Text getDisplayName() {
                    return Text.of("Special");
                }
            };
        } else if (parts[0].equals("grief")) {
            itemGroup = new ItemGroup(ItemGroup.GROUPS.length, "shadow.grief") {
                @Override
                public ItemStack createIcon() {
                    return new ItemStack(Items.TNT);
                }

                @Override
                public void appendStacks(DefaultedList<ItemStack> stacks) {
                    stacks.addAll(stacks);
                }

                @Override
                public Text getDisplayName() {
                    return Text.of("Grief");
                }
            };
        } else {
            itemGroup = new ItemGroup(ItemGroup.GROUPS.length, "shadow.exploit") {
                @Override
                public ItemStack createIcon() {
                    return new ItemStack(Items.ARMOR_STAND);
                }

                @Override
                public void appendStacks(DefaultedList<ItemStack> stacks) {
                    stacks.addAll(stacks);
                }

                @Override
                public Text getDisplayName() {
                    return Text.of("Exploits");
                }
            };
        }
        itemGroup.appendStacks(stacks);
    }
} catch (IOException e) {
    e.printStackTrace();
}
}

