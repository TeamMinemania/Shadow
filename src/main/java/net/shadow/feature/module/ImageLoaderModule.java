package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ArmorStandUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoaderModule extends Module implements PacketOutput {
    public static String text;
    public static String argument;
    final String block = "█";
    final CustomValue<String> url = this.config.create("URL", "");
    final SliderValue size = this.config.create("Size", 70, 10, 500, 0);
    final MultiValue mode = this.config.create("Mode", "ArmorStand", "ArmorStand", "Particle", "Chat");
    BufferedImage imageToBuild;
    int index = 0;
    int placed = 0;
    int skipper = 0;
    Vec3d ppos = null;

    public ImageLoaderModule() {
        super("ImageLoader", "load images into minecraft", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        ppos = Shadow.c.player.getPos();
        placed = 0;
        index = 0;
        loadImage(url.getThis());
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("Particle")) {
            //particle dust 1.000 0.000 0.000 1 ~ ~ ~ 0 0 0 1 1000 normal
            if (skipper % 50 == 0) {
                double px = 0;
                double py = 0;
                for (int index = 0; index < imageToBuild.getHeight(); index++) {
                    for (int i = 0; i < imageToBuild.getWidth(); i++) {
                        var r = new Color(imageToBuild.getRGB(i, index)).getRed();
                        var g = new Color(imageToBuild.getRGB(i, index)).getGreen();
                        var b = new Color(imageToBuild.getRGB(i, index)).getBlue();
                        //ChatUtils.message(r + " " + g + " " + b);
                        double rM = ((r * 1000) / 255);
                        double gM = ((g * 1000) / 255);
                        double bM = ((b * 1000) / 255);
                        px += size.getThis() / 100;
                        //ChatUtils.message("/particle dust " + (rM / 1000D) + " " + (gM / 1000D) + " " + (bM / 1000D) + " 1 " + (ppos.getX() + px) + " " + (ppos.getY() + py) + " " + ppos.getZ() + " 0 0 0 1 5 force");
                        Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/particle dust " + (rM / 1000D) + " " + (gM / 1000D) + " " + (bM / 1000D) + " 500 " + (ppos.getX() + px) + " " + (ppos.getY() + py) + " " + ppos.getZ() + " 0 0 0 1 20 force"));
                    }
                    py -= size.getThis() / 50;
                    px = 0;
                    index++;
                }
            }
            skipper++;
        }
    }

    @Override
    public void onRender() {

    }

    public void loadImage(String imageurl) {
        try {
            URL u = new URL(imageurl);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0");
            huc.connect();
            InputStream is = huc.getInputStream();
            BufferedImage loadedImage = ImageIO.read(is);
            double newWidth = size.getThis();
            double scale = (double) loadedImage.getWidth() / newWidth;
            imageToBuild = resize(loadedImage, (int) (loadedImage.getWidth() / scale), (int) (loadedImage.getHeight() / scale));
            NotificationSystem.notifications.add(new Notification("ImageLoader", "Loaded Image into memory", 100));
            huc.disconnect();
        } catch (Exception ignored) {
            NotificationSystem.notifications.add(new Notification("ImageLoader", "Failed to Loaded Image into memory", 100));
            setEnabled(false);
        }
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket) {
            if (mode.getThis().equalsIgnoreCase("ArmorStand")) {
                ArmorStandUtils.Hologram generated = ArmorStandUtils.generateDefault(text, Vec3d.ZERO).isSmall(false).isEgg(false);
                int max = imageToBuild.getHeight();
                if (index >= max) {
                    setEnabled(false);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.AIR, 1)));
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                for (int i = 0; i < imageToBuild.getWidth(); i++) {
                    int r = imageToBuild.getRGB(i, index);
                    int rP = r & 0xFFFFFF | 0xF000000;
                    builder.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(rP, 16).substring(1)).append("\"},");
                }
                String mc = builder.substring(0, builder.length() - 1) + "]";
                index++;
                generated.text(mc).wrapsName(false);
                generated.position(ppos.add(0, -(index / 4.5f), 0));
                CreativeInventoryActionC2SPacket p = new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, generated.generate());
                Shadow.c.getNetworkHandler().sendPacket(p);
            }
        }
    }
}
