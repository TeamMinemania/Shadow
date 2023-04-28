package net.shadow.scripting;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shadow.Shadow;
import net.shadow.utils.ChatUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    static boolean isParsingFunction = false;
    static List<String> function = new ArrayList<>();
    static String functionname = "";
    static boolean skippingevaluation = false;

    public static void compile(String filename) {
        ChatUtils.message("Loading Script " + filename + ".sha");
        File file = new File(Shadow.c.runDirectory.getAbsolutePath() + "/shadow/scripts/" + filename + ".sha");
        try {
            String real = FileUtils.readFileToString(file, Charsets.UTF_8);
            JsonObject json = new JsonParser().parse(real).getAsJsonObject();
            if (json.has("class")) {
                JsonArray chunkNibbleArray = json.get("class").getAsJsonArray();
                for (JsonElement chunk : chunkNibbleArray) {
                    JsonObject loadedChunk = chunk.getAsJsonObject();
                    if (loadedChunk.has("function")) {
                        JsonObject function = loadedChunk.get("function").getAsJsonObject();
                        String name = function.get("name").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.function(name, payload);
                    } else if (loadedChunk.has("onLoad")) {
                        JsonObject function = loadedChunk.get("onLoad").getAsJsonObject();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.exec(payload);
                    } else if (loadedChunk.has("onCommand")) {
                        JsonObject function = loadedChunk.get("onCommand").getAsJsonObject();
                        String name = function.get("command").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.command(name, payload);
                    } else if (loadedChunk.has("onPhysics")) {
                        JsonObject function = loadedChunk.get("onPhysics").getAsJsonObject();
                        String name = function.get("id").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.addTicker(name, payload);
                    } else if (loadedChunk.has("onLeftClick")) {
                        JsonObject function = loadedChunk.get("onLeftClick").getAsJsonObject();
                        String name = function.get("id").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.addLeftClick(name, payload);
                    } else if (loadedChunk.has("onRightClick")) {
                        JsonObject function = loadedChunk.get("onRightClick").getAsJsonObject();
                        String name = function.get("id").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.addRightClick(name, payload);
                    } else if (loadedChunk.has("onMiddleClick")) {
                        JsonObject function = loadedChunk.get("onMiddleClick").getAsJsonObject();
                        String name = function.get("id").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.addRightClick(name, payload);
                    } else if (loadedChunk.has("onRender")) {
                        JsonObject function = loadedChunk.get("onRender").getAsJsonObject();
                        String name = function.get("id").getAsString();
                        JsonArray payload = function.get("runnable").getAsJsonArray();
                        Executor.addRenderer(name, payload);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String compileChunkAndRun(JsonObject chunk) {
        try {
            String name = chunk.get("c").getAsString();
            return Executor.execute(name, chunk);
        } catch (Exception e) {
            String name = chunk.get("c").getAsString();
            return Executor.execute(name, new JsonObject());
        }
    }
}