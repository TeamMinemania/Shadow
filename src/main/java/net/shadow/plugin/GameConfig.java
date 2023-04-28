package net.shadow.plugin;


import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shadow.Shadow;
import net.shadow.alickgui.element.impl.CategoryDisplay;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.configuration.CustomValue;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class GameConfig {
    static final File CONFIG;
    static final HashMap<String, Integer[]> sender = new HashMap<>();
    static boolean alreadySaved = false;
    static JsonArray oldwindows = null;
    private static boolean loaded = false;

    static {
        CONFIG = new File(Shadow.c.runDirectory.getAbsolutePath() + "/shadow/shadow.conf");
    }

    public static void save() {
        JsonObject base = new JsonObject();
        JsonArray enabled = new JsonArray();
        JsonArray config = new JsonArray();
        for (Module module : ModuleRegistry.getAll()) {
            if (module.isEnabled()) enabled.add(module.getName());
            JsonObject currentConfig = new JsonObject();
            currentConfig.addProperty("name", module.getName());
            JsonArray pairs = new JsonArray();
            for (CustomValue<?> dynamicValue : module.config.returnThis()) {
                JsonObject jesus = new JsonObject();
                jesus.addProperty("key", dynamicValue.getKey());
                jesus.addProperty("value", dynamicValue.getThis() + "");
                pairs.add(jesus);
            }
            currentConfig.add("pairs", pairs);
            config.add(currentConfig);
        }
        JsonArray positions = new JsonArray();
        for (CategoryDisplay d : net.shadow.alickgui.ClickGUI.instance().getElements()) {
            JsonObject pos = new JsonObject();
            pos.addProperty("name", d.getModuleType().getName());
            pos.addProperty("x", d.getX());
            pos.addProperty("y", d.getY());
            positions.add(pos);
        }

        base.add("windows", positions);
        base.add("enabled", enabled);
        base.add("config", config);
        JsonArray f = new JsonArray();
        for (String name : FriendSystem.friendsystem) {
            f.add(name);
        }
        base.add("friends", f);
        System.out.println(base);
        try {
            FileUtils.writeStringToFile(CONFIG, base.toString(), Charsets.UTF_8, false);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save config!");
        }
        ItemsPlugin.save();
    }

    public static void load() {
        if (loaded) return;
        loaded = true;
        try {
            if (!CONFIG.isFile()) CONFIG.delete();
            if (!CONFIG.exists()) return;
            String retrv = FileUtils.readFileToString(CONFIG, Charsets.UTF_8);
            JsonObject config = new JsonParser().parse(retrv).getAsJsonObject();
            if (config.has("config") && config.get("config").isJsonArray()) {
                JsonArray configArray = config.get("config").getAsJsonArray();
                for (JsonElement jsonElement : configArray) {
                    if (jsonElement.isJsonObject()) {
                        JsonObject jobj = jsonElement.getAsJsonObject();
                        String name = jobj.get("name").getAsString();
                        Module j = ModuleRegistry.find(name);
                        if (j == null) continue;
                        if (jobj.has("pairs") && jobj.get("pairs").isJsonArray()) {
                            JsonArray pairs = jobj.get("pairs").getAsJsonArray();
                            for (JsonElement pair : pairs) {
                                JsonObject jo = pair.getAsJsonObject();
                                String key = jo.get("key").getAsString();
                                String value = jo.get("value").getAsString();
                                CustomValue<?> val = j.config.get(key);
                                if (val != null) {
                                    Object newValue = TypeConverter.convert(value, val.getType());
                                    System.out.println(val.getKey() + " = " + value + " (" + newValue + " converted)");
                                    if (newValue != null) val.setValue(newValue);
                                }
                            }
                        }
                    }
                }
            }

            if (config.has("enabled") && config.get("enabled").isJsonArray()) {
                for (JsonElement enabled : config.get("enabled").getAsJsonArray()) {
                    try {
                        String name = enabled.getAsString();
                        Module m = ModuleRegistry.find(name);
                        if (m != null) m.setEnabled(true);
                    } catch (NullPointerException e) {
                        System.out.println("Could not load module, passing over this one");
                    }
                }
            }

            if (config.has("windows") && config.get("windows").isJsonArray()) {
                for (JsonElement window : config.get("windows").getAsJsonArray()) {
                    oldwindows = config.get("windows").getAsJsonArray();
                    JsonObject j = window.getAsJsonObject();
                    String name = j.get("name").getAsString();
                    int x = j.get("x").getAsInt();
                    int y = j.get("y").getAsInt();
                    Integer[] glad = new Integer[3];
                    glad[0] = x; //there HAS to be a better way to do this
                    glad[1] = y;
                    sender.put(name, glad);
                    System.out.println(x + " " + y + " with " + name);
                    net.shadow.alickgui.ClickGUI.setHashMap(sender);
                }
            }
            if (config.has("friends")) {
                JsonArray normal = config.get("friends").getAsJsonArray();
                for (JsonElement thing : normal) {
                    String friend = thing.getAsString();
                    FriendSystem.friendsystem.add(friend);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load config!");
        } finally {
            Keybinds.reload();
        }
    }


    public static boolean isLoaded() {
        return loaded;
    }
}
