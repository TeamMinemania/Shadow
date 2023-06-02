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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
    
            private static final Map<String, Item> itemMap = new HashMap<>();

    public static void loadItems(String filePath) {
        itemMap.put("exploit", Items.ARMOR_STAND);
        itemMap.put("grief", Items.TNT);
        itemMap.put("special", Items.STRUCTURE_VOID);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String section = parts[0];
                    String base64Data = parts[1];

                    Item item = itemMap.get(section);
                    if (item != null) {
                        // Convert base64Data to NBT and create an ItemStack
                        ItemStack stack = getItemStackFromBase64(base64Data);
                        // Add the item to the appropriate section
                        entries.add(new ItemGroupEntry(section, stack));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private static ItemStack getItemStackFromBase64(String base64Data) {
    try {
        // Decode the base64 string to bytes
        byte[] decodedBytes = Base64.decodeBase64(base64Data);
        
        // Read the bytes as an NBT CompoundTag
        CompoundTag nbtTag = NbtIo.readCompressed(new ByteArrayInputStream(decodedBytes));
        
        // Create an ItemStack from the NBT data
        ItemStack stack = ItemStack.fromTag(nbtTag);
        
        return stack;
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    return ItemStack.EMPTY;  // Return an empty ItemStack if an error occurs
}
}

