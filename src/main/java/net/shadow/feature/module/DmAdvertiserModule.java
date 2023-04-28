package net.shadow.feature.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DmAdvertiserModule extends Module {
    int ticks;
    List<String> names = new ArrayList<>();


    public DmAdvertiserModule() {
        super("DmAdvertiser", "advertise some shit in someones dms on mh, works across the whole server", ModuleType.CHAT);
    }

    private static String httpGet(String uri) {
        try {
            URL url = new URL(uri);

            BufferedReader items = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            return items.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 11 == 0) {
            dmrandom();
        }
    }

    @Override
    public void onRender() {

    }

    private void dmrandom() {

    }

    private void getPlayersOnMinehut() {
        NotificationSystem.notifications.add(new Notification("DmAdvertiser", "Getting UUIDS of everyone", 500));
        List<String> uuids = new ArrayList<>();
        JsonArray servers = new JsonParser().parse(httpGet("https://api.minehut.com/servers")).getAsJsonObject().get("servers").getAsJsonArray();
        for (JsonElement server : servers) {
            JsonObject serv = server.getAsJsonObject();
            JsonArray playerarray = serv.get("playerData").getAsJsonObject().get("players").getAsJsonArray();
            for (JsonElement player : playerarray) {
                uuids.add(player.getAsString());
            }
        }
        NotificationSystem.notifications.add(new Notification("DmAdvertiser", "Scraping Usernames..", 500));

    }
}
