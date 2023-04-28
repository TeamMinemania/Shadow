package net.shadow.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsPlugin {
    public static final HashMap<String, ItemStack> itemlist = new HashMap<>();
    static final File ITEMS_FILE;

    static {
        ITEMS_FILE = new File(Shadow.c.runDirectory.getAbsolutePath() + "/shadow/items.json");
        load();
    }

    public static void save() {
        if (itemlist.isEmpty()) {
            return;
        }
        JsonArray saver = new JsonArray();
        for (String key : itemlist.keySet()) {
            JsonObject save = new JsonObject();
            ItemStack ti = itemlist.get(key);
            save.addProperty("identifier", key);
            save.addProperty("name", Registry.ITEM.getId(ti.getItem()).toString());
            save.addProperty("count", ti.getCount() + "");
            if (ti.hasNbt()) {
                save.addProperty("tag", ti.getNbt().asString().replace("Infinity", "1.79769313486232E+308"));
            } else {
                save.addProperty("tag", "{}");
            }

            saver.add(save);
        }
        try {
            FileUtils.writeStringToFile(ITEMS_FILE, saver + "", StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    public static void load() {
        if (!ITEMS_FILE.exists()) {
            return;
        }
        try {
            String fcontent = FileUtils.readFileToString(ITEMS_FILE, StandardCharsets.UTF_8);
            JsonParser parser = new JsonParser();
            JsonElement total = parser.parse(fcontent);
            JsonArray items = total.getAsJsonArray();
            for (JsonElement itemnp : items) {
                JsonObject item = itemnp.getAsJsonObject();
                String identifier = item.get("identifier").getAsString();
                String name = item.get("name").getAsString();
                String count = item.get("count").getAsString();
                String tag = item.get("tag").getAsString();
                ItemStack created = new ItemStack(Registry.ITEM.get(new Identifier(name)), (int) Math.round(Double.parseDouble(count)));
                created.setNbt(StringNbtReader.parse(tag));
                itemlist.put(identifier, created);
            }
        } catch (Exception e) {
            System.out.println("ERROR, ITEMS FILE CORRUPTION");
        }
    }

    public static void add(ItemStack i, String identifier) {
        itemlist.put(identifier, i);
    }

    public static void remove(String identifier) {
        itemlist.remove(identifier);
    }

    public static List<ItemStack> get() {
        List<ItemStack> returnable = new ArrayList<>();
        returnable.addAll(itemlist.values());
        return returnable;
    }
}
