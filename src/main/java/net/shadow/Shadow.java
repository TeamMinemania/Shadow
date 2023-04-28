package net.shadow;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.alickgui.ClickGUI;
import java.awt.image.BufferedImage;
import net.shadow.event.base.EventHandler;
import net.shadow.feature.CommandRegistry;
import net.shadow.feature.ItemRegistry;
import net.shadow.font.Texture;
import net.shadow.gui.SpotlightScreen;
import net.shadow.inter.MClientI;
import net.shadow.plugin.*;
import net.shadow.prisma.APISocket;
import net.shadow.utils.HWID;
import net.shadow.utils.SignUtils;
import net.shadow.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Shadow implements ModInitializer {
    static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();


    //public static boolean allow = false;
    public static String initialUsername = "";
    public static final MinecraftClient c = MinecraftClient.getInstance();
    public static final TeleportService tpService = new TeleportService();
    public static final MClientI clientInterface = (MClientI) MinecraftClient.getInstance();
    public static final SoundEvent MOAN = new SoundEvent(new Identifier("shadow", "moan"));
    public static final SoundEvent ON = new SoundEvent(new Identifier("shadow", "on"));
    public static final SoundEvent OFF = new SoundEvent(new Identifier("shadow", "off"));
    public static final SoundEvent OOF = new SoundEvent(new Identifier("shadow", "blaster"));
    public static APISocket prismaSocket = null;
    public static boolean needsUser = false;

    public static EventHandler e;

    public static final File BASE = new File(MinecraftClient.getInstance().runDirectory, "shadow");

    public void loadTextures(){
        ExecutorService es = Executors.newFixedThreadPool(5);
        for (TextureUtils resource : TextureUtils.values()) {
            es.execute(() -> {
                if(new File(BASE.getPath()+ resource.getWhere().getPath()+".png").exists()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new File(BASE.getPath() + resource.getWhere().getPath()+".png"));
                        registerBufferedImageTexture(resource.getWhere(),bufferedImage);
                    } catch (IOException ignore) {

                    }
                }else{
                    try {
    
                        URL url = new URL(resource.getDownloadUrl());
                        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
                        long completeFileSize = httpConnection.getContentLength();
    
                        BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        byte[] data = new byte[16];
                        int x;
                        while ((x = in.read(data, 0, 16)) >= 0) {
                            bout.write(data, 0, x);
                        }
                        bout.close();
                        in.close();
                        byte[] imageBuffer = bout.toByteArray();
                        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageBuffer));
                        registerBufferedImageTexture(resource.getWhere(), bi);
                        if(!new File(BASE.getPath() + resource.getWhere().getPath()+".png").getParentFile().exists()) {
                            new File(BASE.getPath() + resource.getWhere().getPath()+".png").getParentFile().mkdir();
                        }
                        new File(BASE.getPath()+resource.getWhere().getPath()+".png").createNewFile();
                        FileOutputStream output = new FileOutputStream(BASE.getPath()+resource.getWhere().getPath()+".png");
                        output.write(imageBuffer);
                        output.close();
    
                    } catch (Exception e) {
                        BufferedImage empty = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                        empty.setRGB(0, 0, 0xFF000000);
                        registerBufferedImageTexture(resource.getWhere(), empty);
                    }
                }
            });
        }
    }

    public static EventHandler getEventSystem() {
        return e;
    }

    public static String loadString(String uri) {
        try {
            URL url = new URL(uri);

            BufferedReader items = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            return items.readLine();

        } catch (Exception e) {
            System.err
                    .println("[Shadow] Failed to load items from pastbin!");

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        System.out.println("Shadow Has started!");
        loadTextures();
        ItemRegistry.register();
        CommandRegistry.init();
        e = new EventHandler();
        Keybinds.init();
        SignUtils.initSignText();
        try {
            startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            while (true) {
                Utils.sleep(20);
                if(initialUsername != Shadow.c.getSession().getUsername()) {
                    try {
                        startup();
                    } catch (IOException | InterruptedException ignored) {

                    }
                }
                if (ClickGUI.instance != null) ClickGUI.instance().handleScreenTick();
            }
        }).start();
    }

    public static void startAutoSave() {
        new Thread(() -> {
            while(true) {
                Utils.sleep(60000);
                GameConfig.save();
            }
        }).start();
    }


    public static void startup() throws IOException, InterruptedException {
        String deviceToken = HWID.getHWID();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"client\":\"" + deviceToken + "\", \"username\":\"" + Shadow.c.getSession().getUsername() + "\"}"))
                .setHeader("User-Agent", "ShadowClient /1.0")
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://shadows.pythonanywhere.com/startup")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.body() == "CLIENT_NEEDS_USERNAME") {
            needsUser = true;
        }
        initialUsername = Shadow.c.getSession().getUsername();

    }

    public static void registerBufferedImageTexture(Texture i, BufferedImage bi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            byte[] bytes = baos.toByteArray();

            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
            Shadow.c.execute(() -> Shadow.c.getTextureManager().registerTexture(i, tex));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
