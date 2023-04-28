package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.clickgui.NibletRenderer;
import net.shadow.font.FontRenderers;
import net.shadow.utils.HWID;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CapesGUI extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    TextFieldWidget whurl;
    TextFieldWidget whcont;
    NibletRenderer niblets;
    String acc_username = "";

    public void getAccountUsername(){
        this.acc_username = Shadow.loadString("https://shadows.pythonanywhere.com/username/get?hwid=" + HWID.getHWID());
    }

    protected CapesGUI(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        getAccountUsername();
        niblets = new NibletRenderer(100);
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        whcont = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 50, 300, 20, Text.of("whcont"));
        whcont.setMaxLength(65535);

        whurl = new TextFieldWidget(MC.textRenderer, ww - 150, hh + 25, 300, 20, Text.of("whurl"));
        whurl.setMaxLength(65535);

        ButtonWidget drawblechld = new ButtonWidget(ww - 150, hh - 25, 300, 20, Text.of("Update Cape"), b -> {
            try {
                addCape(whcont.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ButtonWidget usrname = new ButtonWidget(ww - 150, hh + 50, 300, 20, Text.of("Update Username"), b -> {
            try {
                updateUsername(whurl.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.addDrawableChild(drawblechld);
        this.addDrawableChild(usrname);
        super.init();
    }

    private void addCape(String cape) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"client\":\"" + HWID.getHWID() + "\", \"cape\":\"" + cape + "\"}"))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://shadows.pythonanywhere.com/capes/edit")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }


    private void updateUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"client\":\"" + HWID.getHWID() + "\", \"username\":\"" + username + "\"}"))
                .setHeader("User-Agent", "")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://shadows.pythonanywhere.com/username")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            int w = MC.getWindow().getScaledWidth();
            int h = MC.getWindow().getScaledHeight();
            int hh = h / 2;
            int ww = w / 2;
            DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 255).getRGB());
            niblets.rebder(matrices);
            niblets.tickPhysics();
            RenderUtils.renderRoundedQuad(matrices, new Color(20, 20, 20, 255), ww - 210, hh - 70, ww + 175, hh + 85, 5);
            RenderUtils.renderRoundedQuad(matrices, new Color(20, 20, 20, 255), ww - 210, hh - 100, ww + 175, hh - 80, 5);
            FontRenderers.getRenderer().drawString(matrices, "Cape URL", ww - (FontRenderers.getRenderer().getStringWidth("Cape URL")) - 152, hh - 45, 16777215);
            FontRenderers.getRenderer().drawString(matrices, "Account Name: " + acc_username, ww - 200, hh - 95, 16777215);
            FontRenderers.getRenderer().drawString(matrices, "Username", ww - (FontRenderers.getRenderer().getStringWidth("Username")) - 152, hh + 30, 16777215);
            whurl.render(matrices, mouseX, mouseY, delta);
            whcont.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
        });
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
}
