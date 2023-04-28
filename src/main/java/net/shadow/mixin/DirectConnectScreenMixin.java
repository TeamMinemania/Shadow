package net.shadow.mixin;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shadow.font.FontRenderers;
import net.shadow.font.render.FontRenderer;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Requests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(DirectConnectScreen.class)
public abstract class DirectConnectScreenMixin extends Screen {
    @Unique
    private static final Identifier UNKNOWN_SERVER_TEXTURE;
    @Unique
    private static final JsonObject EMPTY_JSON = JsonParser.parseString("{}").getAsJsonObject();
    @Unique
    private static NativeImageBackedTexture nativeImage = null;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).build());
    private static JsonObject currentJson = EMPTY_JSON;

    Requests requests = new Requests();

    static {
        UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
    }

    private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
    private final ServerInfo serverInfo = new ServerInfo(" ", " ", false);
    private long millis = System.currentTimeMillis();
    @Shadow
    private TextFieldWidget addressField;
    @Shadow
    private ButtonWidget selectServerButton;

    protected DirectConnectScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void injectMoveTextField(CallbackInfo ci) {
        addressField.x = selectServerButton.x;
        addressField.y = selectServerButton.y - (addressField.getHeight() + 12);
    }

    @Inject(method = "onAddressFieldChanged", at = @At(value = "TAIL"))
    private void injectPingerAddressAndTime(CallbackInfo ci) {
        serverInfo.name = serverInfo.address = addressField.getText();
        millis = System.currentTimeMillis();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DirectConnectScreen;drawTextWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V", shift = At.Shift.BY))
    private void injectFixTextFieldPos(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int x, int y, int color) {
        drawTextWithShadow(matrixStack, textRenderer, text, addressField.x, addressField.y - textRenderer.fontHeight - 2, color);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void injectPingerTick(CallbackInfo ci) {
        pinger.tick();
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void injectPingerRendering(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long refreshSeconds = 4;
        if (System.currentTimeMillis() - millis >= (refreshSeconds * 1000L)) {
            millis = System.currentTimeMillis();
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    try {
                        currentJson = JsonParser.parseString(requests.get(String.format("http://ip-api.com/json/%s?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,reverse,mobile,proxy,hosting,query", serverInfo.address.isBlank() ? "1.1.1.1" : (serverInfo.address.contains(":") ? serverInfo.address.substring(0, serverInfo.address.indexOf(":")) : serverInfo.address))).body()).getAsJsonObject();
                    } catch (IOException | InterruptedException ignored) {
                        currentJson = EMPTY_JSON;
                    }
                    pinger.cancel();
                    pinger.add(serverInfo, () -> EXECUTOR_SERVICE.execute(() -> {
                        try {
                            byte[] decodedImage = Base64.getMimeDecoder().decode(serverInfo.getIcon());
                            nativeImage = new NativeImageBackedTexture(NativeImage.read(NativeImage.Format.RGBA, new ByteArrayInputStream(decodedImage)));
                        } catch (Throwable e) {
                            nativeImage = null;
                        }
                    }));
                } catch (UnknownHostException e) {
                    LogUtils.getLogger().error(String.format("The address \"%s\" couldn't be pinged: ", addressField.getText()), e);
                }
            });
        }
        int rgbaBackground = 0x45FFFFFF;
        int width = 305;
        int height = 36;
        int logoSize = 32;
        matrices.push();
        matrices.translate(addressField.x + addressField.getWidth() / 2.F - width / 2.F, addressField.y - height - 26, 0);
        Shader previousShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderUtils.renderRoundedQuad(matrices, new Color(15, 15, 15, 255),0, 0, width, height, 5);
        float u = .0F, v = .0F;
        if (nativeImage != null) RenderSystem.setShaderTexture(0, nativeImage.getGlId());
        else {
            u = .0F;
            v = serverInfo.ping < 0L ? 5 : serverInfo.ping < 150L ? 0 : serverInfo.ping < 300L ? 1 : serverInfo.ping < 600L ? 2 : serverInfo.ping < 1000L ? 3 : 4;
            RenderSystem.setShaderTexture(0, UNKNOWN_SERVER_TEXTURE);
        }
        matrices.translate(0, height / 2.d - logoSize / 2.d, 0);
        fill(matrices, 2, 0, 2 + logoSize, logoSize, rgbaBackground);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        drawTexture(matrices, 2, 0, u, v, logoSize, logoSize, logoSize, logoSize);
        RenderSystem.disableBlend();
        fill(matrices, 0, height - 1, (int) (width * ((System.currentTimeMillis() - millis) / (float) (refreshSeconds * 1000L))), height, -1);
        matrices.translate(logoSize, 0, 0);
        FontRenderers.getRenderer().drawString(matrices, serverInfo.name, 5, 1, -1);
        List<String> texts = serverInfo.label != null ? List.of(wrapText(serverInfo.label.getString())) : List.of(new String[]{"Loading..."});
        for (int i = 0; i < Math.min(texts.size(), 2); i++) FontRenderers.getRenderer().drawString(matrices, texts.get(i), 5, 12 + (9 * i), 0x808080);
        RenderSystem.setShader(() -> previousShader);
        matrices.pop();
        matrices.push();
        matrices.translate(addressField.x + addressField.getWidth() / 2.F - width / 2.F, addressField.y - height - 26, 0);
        FontRenderers.getRenderer().drawString(matrices, String.format("%.1fs", ((System.currentTimeMillis() - millis) / 1000.F)), width + 4, height - client.textRenderer.fontHeight, -1);
        matrices.pop();
        if (currentJson != EMPTY_JSON) {
            matrices.push();
            float scale = .75F;
            matrices.scale(scale, scale, 0);
            Set<Map.Entry<String, JsonElement>> currentJsonEntrySet = currentJson.entrySet();
            Map<String, JsonElement> currentJsonMap = new Object2ObjectArrayMap<>();
            List<String> currentJsonLineList = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : currentJsonEntrySet) {
                if (entry.getKey().isBlank() || entry.getValue().getAsString().isBlank()) continue;
                currentJsonMap.put(entry.getKey(), entry.getValue());
                currentJsonLineList.add(String.format("%s: %s", entry.getKey(), entry.getValue()));
            }
            matrices.translate(4, this.height / 2.F - (((((currentJsonMap.size() * (client.textRenderer.fontHeight + 2)) * scale) / 2.F))), 0);
            fill(matrices, 0, -3, client.textRenderer.getWidth(getLongestString(currentJsonLineList)) + 6, currentJsonMap.size() * (client.textRenderer.fontHeight + 2), rgbaBackground);
            int yOffset = 0;
            for (Map.Entry<String, JsonElement> entry : currentJsonMap.entrySet()) {
                if (entry.getKey().isBlank() || entry.getValue().getAsString().isBlank()) continue;
                FontRenderers.getRenderer().drawString(matrices, String.format("%s%s%s: %s", Formatting.WHITE, entry.getKey().toUpperCase(), Formatting.GRAY, entry.getValue()), 4, yOffset, -1);
                yOffset += client.textRenderer.fontHeight + 2;
            }
            matrices.pop();
        }
    }

    private String[] wrapText(String text){
        StringBuilder sb = new StringBuilder(text);

        int i = 0;
        while (i + 20 < sb.length() && (i = sb.lastIndexOf(" ", i + 60)) != -1) {
            sb.replace(i, i + 1, "\n");
        }

        return sb.toString().split("\n");
    }

    private String getLongestString(List<String> array) {
        int maxLength = 0;
        String longestString = null;
        for (String s : array) {
            if (s.length() > maxLength) {
                maxLength = s.length();
                longestString = s;
            }
        }
        return longestString;
    }
}