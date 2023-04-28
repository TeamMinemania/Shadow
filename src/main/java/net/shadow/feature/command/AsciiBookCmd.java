package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsciiBookCmd extends Command {
    final String block = "█";
    final String unblock = "⠀";

    BufferedImage imageToBuild;

    public AsciiBookCmd() {
        super("imagebook", "make a book with ascii art in it");
    }

    @Override
    public void call(String[] args) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
        StringBuilder page = new StringBuilder();
        loadImage(args[0]);
        int max = imageToBuild.getHeight();
        for (int index = 0; index < max; index++) {
            for (int i = 0; i < imageToBuild.getWidth(); i++) {
                int r = imageToBuild.getRGB(i, index);
                int hex = r & 0xFFFFFF | 0xF000000;
                page.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(hex, 16).substring(1)).append("\"},");
            }
            page.append("{\"text\":\"\\\\n\"},");
        }
        String loader = page.substring(0, page.length() - 1);
        try {
            book.setNbt(StringNbtReader.parse("{title:\"\",author:\"ImageBook\",pages:['[" + loader + "]']}"));
        } catch (Exception ignored) {
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, book));
        NotificationSystem.notifications.add(new Notification("ImageBook", "Put image in book", 100));
    }

    public void loadImage(String imageurl) {
        try {
            URL u = new URL(imageurl);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0");
            huc.connect();
            InputStream is = huc.getInputStream();
            BufferedImage loadedImage = ImageIO.read(is);
            double scalew = (double) loadedImage.getWidth() / 12;
            double scaleh = (double) loadedImage.getHeight() / 15;
            imageToBuild = resize(loadedImage, (int) (loadedImage.getWidth() / scalew), (int) (loadedImage.getHeight() / scaleh));
            huc.disconnect();
        } catch (Exception ignored) {
            NotificationSystem.notifications.add(new Notification("ImageLoader", "Failed to Loaded Image into memory", 100));
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
}
