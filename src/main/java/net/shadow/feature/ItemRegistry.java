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
        // Grief Items
        FabricItemGroupBuilder.create(
                        new Identifier("shadow", "grief"))
                .icon(() -> new ItemStack(Items.TNT))
                .appendItems(stacks -> {
                    stacks.addAll(grief);
                })
                .build();
        // Exploit Items
        FabricItemGroupBuilder.create(
                        new Identifier("shadow", "exploit"))
                .icon(() -> new ItemStack(Items.ARMOR_STAND))
                .appendItems(stacks -> {
                    stacks.addAll(exploit);
                })
                .build();
        // Custom Items
        FabricItemGroupBuilder.create(
                        new Identifier("shadow", "custom"))
                .icon(() -> new ItemStack(Items.CHEST))
                .appendItems(stacks -> {
                    if (GlobalConfig.accessing.equals("Logger")) {
                        stacks.addAll(CustomStacksPlugin.getStacks());
                    } else if (GlobalConfig.accessing.equals("Items")) {
                        stacks.addAll(ItemsPlugin.get());
                    }
                })
                .build();

    }
    private static List<ItemStack> loadGriefItems() {
        
        String data = loadString("https://shadows.pythonanywhere.com/items/grief/list");
        JsonObject response = new JsonParser().parse(data).getAsJsonObject();
        JsonArray items = response.get("items").getAsJsonArray();
        List<ItemStack> localItems = new ArrayList<ItemStack>();
        for (JsonElement itemProto : items) {
            JsonObject item = itemProto.getAsJsonObject();
            ItemStack loadedItem = new ItemStack(Registry.ITEM.get(new Identifier(item.get("name").getAsString())), Integer.parseInt(item.get("count").getAsString()));
            String nbt = new String(Base64.getDecoder().decode(item.get("nbt").getAsString()));
            try {
                loadedItem.setNbt(StringNbtReader.parse(nbt));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            }
            localItems.add(loadedItem);
        }
        return localItems;
    }
    private static List<ItemStack> loadExploitItems() {
        String data = loadString("https://shadows.pythonanywhere.com/items/exploits/list");
        JsonObject response = new JsonParser().parse(data).getAsJsonObject();
        JsonArray items = response.get("items").getAsJsonArray();
        List<ItemStack> localItems = new ArrayList<ItemStack>();
        for (JsonElement itemProto : items) {
            JsonObject item = itemProto.getAsJsonObject();
            ItemStack loadedItem = new ItemStack(Registry.ITEM.get(new Identifier(item.get("name").getAsString())), Integer.parseInt(item.get("count").getAsString()));
            String nbt = new String(Base64.getDecoder().decode(item.get("nbt").getAsString()));
            try {
                loadedItem.setNbt(StringNbtReader.parse(nbt));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                System.exit(1);
            }
            localItems.add(loadedItem);
        }
        return localItems;
    }
}
