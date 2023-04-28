package net.shadow.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MinehutGui extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    TextFieldWidget whurl;

    protected MinehutGui(Text title) {
        super(title);
    }

    private static String loadString(String uri) {
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
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        whurl = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 50, 300, 20, Text.of("whurl"));
        whurl.setMaxLength(65535);

        ButtonWidget start = new ButtonWidget(ww - 150, hh - 25, 300, 20, Text.of("Plugins"), button -> new Thread(() -> {
            try {
                NotificationSystem.notifications.add(new Notification("Plugins", "Grabbing Server Info", 50));
                String serverdata = loadString("https://api.minehut.com/server/" + whurl.getText() + "?byName=true");
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
                String complist = "";
                for (String item : server) {
                    complist += ", " + item;
                }
                ChatUtils.message("Server Plugins [" + server.size() + "] : " + complist);
            } catch (Exception e) {
                e.printStackTrace();
                NotificationSystem.notifications.add(new Notification("Plugins", "Something went wrong while grabbing plugins", 50));
            }
        }).start());

        ButtonWidget spam = new ButtonWidget(ww - 150, hh + 25, 300, 20, Text.of("Players"), button -> {


        });

        ButtonWidget delete = new ButtonWidget(ww - 150, hh, 300, 20, Text.of("Data"), button -> {
        });

        this.addDrawableChild(start);
        this.addDrawableChild(spam);
        this.addDrawableChild(delete);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 55).getRGB());
        RenderUtils.renderRoundedQuad(matrices, new Color(52, 52, 52, 255), ww - 175, hh - 60, ww + 175, hh + 60, 5);
        whurl.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        whurl.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        whurl.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        whurl.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        whurl.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
