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

import static net.shadow.Shadow.loadString;

public class ItemRegistry {

    static List<ItemStack> grief = loadGriefItems();
    static List<ItemStack> exploit = loadExploitItems();

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
