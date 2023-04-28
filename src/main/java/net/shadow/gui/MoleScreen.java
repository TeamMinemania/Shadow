package net.shadow.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.inter.IMultiplayerScreen;
import net.shadow.plugin.GlobalConfig;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;

public class MoleScreen extends Screen {
    static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    private final MultiplayerScreen MPInstance;
    TextFieldWidget filter;
    TextFieldWidget filterversion;
    String status = "Waiting...";


    protected MoleScreen(Text title, MultiplayerScreen multiplayerScreenInstance) {
        super(title);
        MPInstance = multiplayerScreenInstance;
    }


    @Override
    public void init() {
        int w = Shadow.c.getWindow().getScaledWidth();
        int h = Shadow.c.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        filter = new TextFieldWidget(Shadow.c.textRenderer, ww - 150, hh - 75, 300, 20, Text.of("shitassretard"));
        filter.setMaxLength(65535);
        filter.setText(GlobalConfig.search1);
        filterversion = new TextFieldWidget(Shadow.c.textRenderer, ww - 150, hh - 50, 300, 20, Text.of("shitassretard2"));
        filterversion.setMaxLength(65535);
        filterversion.setText(GlobalConfig.search2);
        status = "Waiting...";

        ButtonWidget delete = new ButtonWidget(ww - 150, hh + 25, 300, 20, Text.of("Search"), button -> new Thread(() -> {
            try {
                status = "Grabbing Servers";
                String loader = requestServers(filterversion.getText(), filter.getText(), Boolean.parseBoolean(GlobalConfig.version));
                JsonArray servers = new JsonParser().parse(loader).getAsJsonObject().get("servers").getAsJsonArray();
                status = "Returned " + servers.size() + " Servers";
                Utils.sleep(500);
                int amount;
                try {
                    amount = Integer.parseInt(GlobalConfig.amount);
                } catch (NumberFormatException e) {
                    amount = Integer.MAX_VALUE;
                }
                ArrayList<String> serverlist = new ArrayList<>();
                for (JsonElement elem : servers) {
                    serverlist.add(elem.getAsString());
                }
                Collections.shuffle(serverlist);
                for (String server : serverlist) {
                    status = "Adding Servers: " + amount;
                    amount--;
                    if (amount < 1) continue;
                    MPInstance.getServerList().add(new ServerInfo("Molenheimer: " + server, server, false));
                    ((IMultiplayerScreen) MPInstance).getServerListSelector().setSelected(null);
                    ((IMultiplayerScreen) MPInstance).getServerListSelector().setServers(MPInstance.getServerList());
                    Utils.sleep(1);
                }
                status = "Done!";
                Shadow.c.setScreen(MPInstance);
                MPInstance.getServerList().saveFile();
            } catch (Exception ignored) {
            }
        }).start());

        ButtonWidget versionpreferred = new ButtonWidget(ww - 150, hh - 25, 300, 20, Text.of("Has Players: " + GlobalConfig.version), button -> {
            if (GlobalConfig.version.equals("True")) {
                GlobalConfig.version = "False";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
                return;
            }
            if (GlobalConfig.version.equals("False")) {
                GlobalConfig.version = "True";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
            }
        });

        ButtonWidget maxamount = new ButtonWidget(ww - 150, hh, 300, 20, Text.of("Maximum: " + GlobalConfig.amount), button -> {
            if (GlobalConfig.amount.equals("50")) {
                GlobalConfig.amount = "100";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
                return;
            }
            if (GlobalConfig.amount.equals("100")) {
                GlobalConfig.amount = "500";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
                return;
            }
            if (GlobalConfig.amount.equals("500")) {
                GlobalConfig.amount = "1000";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
                return;
            }
            if (GlobalConfig.amount.equals("1000")) {
                GlobalConfig.amount = "Infinity";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
                return;
            }
            if (GlobalConfig.amount.equals("Infinity")) {
                GlobalConfig.amount = "50";
                Shadow.c.setScreen(new MoleScreen(title, MPInstance));
            }
        });


        this.addDrawableChild(delete);
        this.addDrawableChild(versionpreferred);
        this.addDrawableChild(maxamount);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            int w = Shadow.c.getWindow().getScaledWidth();
            int h = Shadow.c.getWindow().getScaledHeight();
            int hh = h / 2;
            int ww = w / 2;
            DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 255).getRGB());
            FontRenderers.getRenderer().drawCenteredString(matrices, "Molenheimer", ww, hh - 120, new Color(255, 255, 255, 255).getRGB());
            RenderUtils.renderRoundedQuad(matrices, new Color(22, 22, 22, 255), ww - 175, hh - 100, ww + 175, hh + 60, 10);
            FontRenderers.getRenderer().drawCenteredString(matrices, "Status: " + status, ww, hh + 75, new Color(255, 255, 255, 255).getRGB());
            filter.render(matrices, mouseX, mouseY, delta);
            filterversion.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
        });
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        filter.charTyped(chr, keyCode);
        filterversion.charTyped(chr, keyCode);
        GlobalConfig.search1 = filter.getText();
        GlobalConfig.search2 = filterversion.getText();
        System.out.println(GlobalConfig.search1 + "called" + GlobalConfig.search2);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        filter.keyReleased(keyCode, scanCode, modifiers);
        filterversion.keyReleased(keyCode, scanCode, modifiers);
        GlobalConfig.search1 = filter.getText();
        GlobalConfig.search2 = filterversion.getText();
        System.out.println(GlobalConfig.search1 + "called" + GlobalConfig.search2);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        filter.keyPressed(keyCode, scanCode, modifiers);
        filterversion.keyPressed(keyCode, scanCode, modifiers);
        GlobalConfig.search1 = filter.getText();
        GlobalConfig.search2 = filterversion.getText();
        System.out.println(GlobalConfig.search1 + "called" + GlobalConfig.search2);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        filter.mouseClicked(mouseX, mouseY, button);
        filterversion.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String requestServers(String version, String motdincludes, boolean hasplayers) throws IOException, InterruptedException {
        //{"secret":"molekey0", "motd":"", "version":"1.17.1", "players":"none"}
        String hasp;
        if (hasplayers) {
            hasp = "needs";
        } else {
            hasp = "none";
        }
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"secret\":\"molekey0\", \"motd\":\"" + motdincludes + "\", \"version\":\"" + version + "\", \"players\":\"" + hasp + "\"}"))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://shadows.pythonanywhere.com/servers/search")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}