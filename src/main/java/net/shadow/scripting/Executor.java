package net.shadow.scripting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Random;

public class Executor {
    public static final HashMap<String, String> memoryTable = new HashMap<>();
    public static final HashMap<String, JsonArray> functionTable = new HashMap<>();
    public static final HashMap<String, JsonArray> commandsTable = new HashMap<>();
    public static final HashMap<String, JsonArray> leftClickTable = new HashMap<>();
    public static final HashMap<String, JsonArray> rightClickTable = new HashMap<>();
    public static final HashMap<String, JsonArray> renderTable = new HashMap<>();
    public static final HashMap<String, JsonArray> middleClickTable = new HashMap<>();
    public static final HashMap<String, JsonArray> tickFunctionTable = new HashMap<>();

    static final HttpClient hclient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public static void function(String name, JsonArray payload) {
        functionTable.put(name, payload);
    }

    public static void addRenderer(String name, JsonArray payload) {
        renderTable.remove(name);
        renderTable.put(name, payload);
    }

    public static void addMiddleClick(String name, JsonArray payload) {
        middleClickTable.put(name, payload);
    }

    public static void addTicker(String name, JsonArray payload) {
        tickFunctionTable.remove(name);
        tickFunctionTable.put(name, payload);
    }

    public static void addLeftClick(String name, JsonArray payload) {
        leftClickTable.remove(name);
        leftClickTable.put(name, payload);
    }

    public static void addRightClick(String name, JsonArray payload) {
        rightClickTable.remove(name);
        rightClickTable.put(name, payload);
    }

    public static void removeTicker(String name) {
        tickFunctionTable.remove(name);
    }

    public static void command(String name, JsonArray payload) {
        commandsTable.remove(name);
        commandsTable.put(name, payload);
    }


    public static void functionCall(String name) {
        if (functionTable.containsKey(name)) {
            for (JsonElement chunk : functionTable.get(name)) {
                Parser.compileChunkAndRun(chunk.getAsJsonObject());
            }
        }
    }

    public static void exec(JsonArray payload) {
        for (JsonElement chunk : payload) {
            Parser.compileChunkAndRun(chunk.getAsJsonObject());
        }
    }


    public static String execute(String namecall, JsonObject data) {
        if (functionTable.containsKey(namecall)) {
            functionCall(namecall);
            return "";
        }
        switch (namecall) {
            case "chat":
                Shadow.c.player.sendChatMessage(extractStrJson(data, "message"));
                break;

            case "echo":
                ChatUtils.message(extractStrJson(data, "message"));
                break;

            case "multiply":
                return (Double.parseDouble(extractStrJson(data, "a")) * Double.parseDouble(extractStrJson(data, "b"))) + "";

            case "divide":
                return (Double.parseDouble(extractStrJson(data, "a")) / Double.parseDouble(extractStrJson(data, "b"))) + "";

            case "subtract":
                return (Double.parseDouble(extractStrJson(data, "a")) - Double.parseDouble(extractStrJson(data, "b"))) + "";

            case "add":
                return (Double.parseDouble(extractStrJson(data, "a")) + Double.parseDouble(extractStrJson(data, "b"))) + "";

            case "concat":
                return extractStrJson(data, "a") + extractStrJson(data, "b");

            case "renderBlock":
                int x = extractIntJson(data, "x");
                int y = extractIntJson(data, "y");
                int z = extractIntJson(data, "z");
                RenderUtils.renderObject(new Vec3d(x, y, z), new Vec3d(1, 1, 1), new Color(255, 255, 255, 255), new MatrixStack());
                break;

            case "join":
                JsonArray pload = data.get("strings").getAsJsonArray();
                StringBuilder returner = new StringBuilder();
                for (JsonElement p : pload) {
                    returner.append(extractNoGet(p));
                }
                return returner.toString();

            case "set":
                memoryTable.put(extractStrJson(data, "var"), extractStrJson(data, "value"));
                break;


            case "unEvent":
                tickFunctionTable.remove(extractStrJson(data, "id"));
                leftClickTable.remove(extractStrJson(data, "id"));
                rightClickTable.remove(extractStrJson(data, "id"));
                break;

            case "get":
                return memoryTable.get(extractStrJson(data, "var"));

            case "var_dump":
                return memoryTable.keySet().toString();


            case "for":
                int amount = extractIntJson(data, "amount");
                JsonArray runable = data.get("runnable").getAsJsonArray();
                for (int i = 0; i < amount; i++) {
                    exec(runable);
                }
                break;

            case "if":
                boolean should4 = Boolean.parseBoolean(extractStrJson(data, "eval"));
                if (should4) {
                    exec(data.get("runnable").getAsJsonArray());
                }
                break;


            case "updatePosition":
                Shadow.c.player.updatePosition(extractIntJson(data, "x"), extractIntJson(data, "y"), extractIntJson(data, "z"));
                break;

            case "addVelocity":
                Shadow.c.player.addVelocity(extractIntJson(data, "x"), extractIntJson(data, "y"), extractIntJson(data, "z"));
                break;

            case "setVelocity":
                Shadow.c.player.setVelocity(extractIntJson(data, "x"), extractIntJson(data, "y"), extractIntJson(data, "z"));
                break;

            case "swingHand":
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                break;

            case "rotate":
                Shadow.c.player.setPitch(extractIntJson(data, "pitch"));
                Shadow.c.player.setYaw(extractIntJson(data, "yaw"));
                break;

            case "random":
                return new Random().nextInt(extractIntJson(data, "upper")) + "";

            case "split":
                String[] value = extractStrJson(data, "string").split(extractStrJson(data, "regex"));
                int i = 0;
                for (String s : value) {
                    i++;
                    memoryTable.put(extractStrJson(data, "out") + "-" + i, s);
                }
                break;


            case "lambda":
                JsonArray runable4 = data.get("runnable").getAsJsonArray();
                exec(runable4);
                break;

            case "equals":
                String a = extractStrJson(data, "a");
                String b = extractStrJson(data, "b");
                if (a.equals(b)) {
                    return "true";
                } else {
                    return "false";
                }


            case "sleep":
                int time = extractIntJson(data, "time");
                Utils.sleep(time);
                break;

            case "while":
                boolean should = true;
                while (should) {
                    should = Boolean.parseBoolean(extractStrJson(data, "eval"));
                    exec(data.get("runnable").getAsJsonArray());
                }
                break;


            case "request":
                if (!extractArg(data, "async")) {
                    if (extractStrJson(data, "mode").equals("POST")) {
                        return httpPOST(extractStrJson(data, "payload"), extractStrJson(data, "url"));
                    } else {
                        return httpGET(extractStrJson(data, "url"));
                    }
                }
                break;

            case "env":
                return playerEnv(extractStrJson(data, "key"));


            case "async":
                new Thread(() -> exec(data.get("runnable").getAsJsonArray())).start();
                break;


            case "packet":
                String type = extractStrJson(data, "type");
                switch (type) {
                    case "player_move":

                    case "vehicle_move":

                    case "player_interact":

                    case "player_interact_entity":

                    case "player_interact_block":

                    case "player_input":

                    case "custom_payload":

                    case "creative_inventory_action":

                    case "client_commands":

                    case "client_settings":

                    case "client_command":
                        break;

                    case "hand_swing":
                        Shadow.c.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                        break;

                    case "keep_alive":
                        int id = extractIntJson(data, "id");
                        Shadow.c.player.networkHandler.sendPacket(new KeepAliveC2SPacket(id));
                        break;

                    case "player_action":
                        String action = extractStrJson(data, "action");
                        Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.valueOf(action), new BlockPos(0, 0, 0), Direction.UP));
                        break;

                    case "tab_complete":
                        int syncid = extractIntJson(data, "syncid");
                        String partialCommand = extractStrJson(data, "command");
                        Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(syncid, partialCommand));
                        break;
                }
                break;

            case "runtime_string":
                return "hello custom string in compiler world";
        }

        return "";
    }

    public static String extractStrJson(JsonObject data, String key) {
        JsonElement ae = data.get(key);
        if (ae instanceof JsonObject) {
            return Parser.compileChunkAndRun(ae.getAsJsonObject());
        } else {
            return ae.getAsString();
        }
    }

    public static String extractNoGet(JsonElement data) {
        if (data instanceof JsonObject) {
            return Parser.compileChunkAndRun(data.getAsJsonObject());
        } else {
            return data.getAsString();
        }
    }

    public static Integer extractIntJson(JsonObject data, String key) {
        JsonElement ae = data.get(key);
        try {
            if (ae instanceof JsonObject) {
                return (int) Math.round(Double.parseDouble(Parser.compileChunkAndRun(ae.getAsJsonObject())));
            } else {
                if (((Object) ae) instanceof String) {
                    try {
                        return (int) Math.round(Double.parseDouble(ae.getAsString()));
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                }
                return ae.getAsInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean extractArg(JsonObject data, String key) {
        if (data.has(key)) {
            return data.get(key).getAsBoolean();
        } else {
            return false;
        }
    }

    private static String httpPOST(String json, String uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("User-Agent", "")
                    .setHeader("Content-Type", "application/json")
                    .uri(URI.create(uri)).build();
            HttpResponse<String> response = hclient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "{ERROR}";
        }

    }

    private static String httpGET(String uri) {
        try {
            URL url = new URL(uri);

            BufferedReader items = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            return items.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private static String playerEnv(String env) {
        switch (env) {
            case "player.x":
                return ((int) Math.round(Shadow.c.player.getX())) + "";

            case "player.y":
                return ((int) Math.round(Shadow.c.player.getY())) + "";

            case "player.z":
                return ((int) Math.round(Shadow.c.player.getZ())) + "";

            case "cursor.entity":
                if (!(Shadow.c.crosshairTarget instanceof EntityHitResult)) {
                    return "None";
                }
                return ((EntityHitResult) Shadow.c.crosshairTarget).getEntity().getUuidAsString();
        }
        return "INVALID_IDENTIFIER";
    }
}
