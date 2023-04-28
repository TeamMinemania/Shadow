package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.font.FontRenderers;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebhooksGui extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    TextFieldWidget whurl;
    TextFieldWidget whcont;

    protected WebhooksGui(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        whurl = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 50, 300, 20, Text.of("whurl"));
        whurl.setMaxLength(65535);
        whcont = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 75, 300, 20, Text.of("whcont"));
        whcont.setMaxLength(65535);

        ButtonWidget start = new ButtonWidget(ww - 150, hh - 25, 300, 20, Text.of("Send"), button -> {
            String[] webhooks = whurl.getText().split(";");
            new Thread(() -> {
                for (String webhook : webhooks) {
                    try {
                        if (send(whcont.getText(), webhook)) {
                            NotificationSystem.notifications.add(new Notification("Webhooks", "Sent Payload to webhook", 150));
                        } else {
                            NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                        }
                    } catch (IOException | InterruptedException e) {
                        NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                    }
                }
            }).start();
        });

        ButtonWidget spam = new ButtonWidget(ww - 150, hh + 25, 300, 20, Text.of("Spam"), button -> {
            String[] webhooks = whurl.getText().split(";");
            new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    for (String webhook : webhooks) {
                        try {
                            if (send(whcont.getText(), webhook)) {
                                NotificationSystem.notifications.add(new Notification("Webhooks", "Sent Payload to webhook", 150));
                            } else {
                                NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                            }
                        } catch (IOException | InterruptedException e) {
                            NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to Send Payload to webhook", 150));
                        }
                    }
                }
            }).start();
        });

        ButtonWidget delete = new ButtonWidget(ww - 150, hh, 300, 20, Text.of("Delete"), button -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .setHeader("User-Agent", "")
                    .setHeader("Content-Type", "application/json")
                    .uri(URI.create(whurl.getText())).build();
            try {
                HttpResponse<String> callback = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (callback.statusCode() == 200) {
                    NotificationSystem.notifications.add(new Notification("Webhooks", "Deleted Webhook", 150));
                } else {
                    NotificationSystem.notifications.add(new Notification("Webhooks", "Failed to delete webhook", 150));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

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
        RenderUtils.renderRoundedQuad(matrices, new Color(52, 52, 52, 255), ww - 250, hh - 100, ww + 175, hh + 60, 5);
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 55).getRGB());
        FontRenderers.getRenderer().drawString(matrices, "Webhook URL", ww - (FontRenderers.getRenderer().getStringWidth("Webhook URL")) - 150, hh - 50, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "Webhook Content", ww - (FontRenderers.getRenderer().getStringWidth("Webhook Content")) - 150, hh - 75, 16777215);
        whurl.render(matrices, mouseX, mouseY, delta);
        whcont.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        whurl.charTyped(chr, keyCode);
        whcont.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        whurl.keyReleased(keyCode, scanCode, modifiers);
        whcont.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        whurl.keyPressed(keyCode, scanCode, modifiers);
        whcont.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        whurl.mouseClicked(mouseX, mouseY, button);
        whcont.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //this is definitely not skidded from 0x150, dont even bother looking, just dont.
    boolean send(String message, String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"content\": \"" + message + "\"}"))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create(uri)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 204;
    }
}
