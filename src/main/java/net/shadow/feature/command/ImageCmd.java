package net.shadow.feature.command;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageCmd extends Command implements PacketInput {
    //10 x 4 per sign
    final String block = "█";
    final String unblock = "⠀";
    BufferedImage imageToBuild;

    public ImageCmd() {
        super("image", "write frames to text");
    }
    

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"chat", "book", "lore"});
        }
        if(index == 1){
            return List.of(new String[]{"https://discord.com/image.png"});
        }
        if(index == 2){
            return List.of(new String[]{"25"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 3) {
            ChatUtils.message("Please Use >image <mode> <url> <size>, or >image help");
            return;
        }
        switch (args[0]) {
            case "help" -> {
                ChatUtils.message("Modes:");
                ChatUtils.message(">image chat");
                ChatUtils.message(">image book");
                ChatUtils.message(">image lore");
            }
            case "chat" -> new Thread(() -> {
                try {
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    loadImage(args[1], Integer.parseInt(args[2]));
                    int max = imageToBuild.getHeight();
                    for (int index = 0; index < max; index++) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("[");
                        for (int i = 0; i < imageToBuild.getWidth(); i++) {
                            int r = imageToBuild.getRGB(i, index);
                            int rP = r & 0xFFFFFF | 0xF000000;
                            builder.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(rP, 16).substring(1)).append("\"},");
                        }
                        String mc = builder.substring(0, builder.length() - 1) + "]";
                        Utils.sleep(50);
                        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(((BlockHitResult) Shadow.c.crosshairTarget).getBlockPos(), "REST", CommandBlockBlockEntity.Type.REDSTONE, false, false, false));
                        Utils.sleep(50);
                        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(((BlockHitResult) Shadow.c.crosshairTarget).getBlockPos(), "/execute run tellraw @a " + mc, CommandBlockBlockEntity.Type.REDSTONE, false, false, true));
                    }
                } catch (Exception e) {
                    NotificationSystem.notifications.add(new Notification("ChatPrinter", "Cant Load Image?", 150));
                }
                Utils.sleep(2000);
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }).start();
            case "chat2" -> new Thread(() -> {
                try {
                    Shadow.getEventSystem().add(PacketInput.class, this);
                    loadImage(args[1], Integer.parseInt(args[2]));
                    int max = imageToBuild.getHeight();
                    for (int index = 0; index < max; index++) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("[");
                        for (int i = 0; i < imageToBuild.getWidth(); i++) {
                            int r = imageToBuild.getRGB(i, index);
                            int rP = r & 0xFFFFFF | 0xF000000;
                            builder.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(rP, 16).substring(1)).append("\"},");
                        }
                        String mc = builder.substring(0, builder.length() - 1) + "]";
                        Utils.sleep(50);
                        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(((BlockHitResult) Shadow.c.crosshairTarget).getBlockPos(), "REST", CommandBlockBlockEntity.Type.REDSTONE, false, false, false));
                        Utils.sleep(50);
                        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(((BlockHitResult) Shadow.c.crosshairTarget).getBlockPos(), "/tellraw @a " + mc, CommandBlockBlockEntity.Type.REDSTONE, false, false, true));
                    }
                } catch (Exception e) {
                    NotificationSystem.notifications.add(new Notification("ChatPrinter", "Cant Load Image?", 150));
                }
                Utils.sleep(2000);
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }).start();
            case "lore" -> {
                ItemStack item = Shadow.c.player.getMainHandStack();
                StringBuilder page = new StringBuilder();
                loadImage(args[1], Integer.parseInt(args[2]));
                int max = imageToBuild.getHeight();
                for (int index = 0; index < max; index++) {
                    StringBuilder lamo = new StringBuilder();
                    for (int i = 0; i < imageToBuild.getWidth(); i++) {
                        int r = imageToBuild.getRGB(i, index);
                        int hex = r & 0xFFFFFF | 0xF000000;
                        lamo.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(hex, 16).substring(1)).append("\",\"italic\":false},");
                    }
                    String lamopage = lamo.substring(0, lamo.length() - 1);
                    page.append("'[" + lamopage + "]'" + ",");
                }
                String loader = page.substring(0, page.length() - 1);
                try {
                    item.getOrCreateNbt().copyFrom(StringNbtReader.parse("{display:{Lore:[" + loader + "]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, item));
                NotificationSystem.notifications.add(new Notification("ImageBook", "Put image on item lore", 100));
            }
            case "book" -> {
                ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
                StringBuilder pager = new StringBuilder();
                loadImage(args[1]);
                int ma2x = imageToBuild.getHeight();
                for (int index = 0; index < ma2x; index++) {
                    for (int i = 0; i < imageToBuild.getWidth(); i++) {
                        int r = imageToBuild.getRGB(i, index);
                        int hex = r & 0xFFFFFF | 0xF000000;
                        pager.append("{\"text\":\"").append(block).append("\",\"color\":\"#").append(Integer.toString(hex, 16).substring(1)).append("\"},");
                    }
                    pager.append("{\"text\":\"\\\\n\"},");
                }
                String loaderstr = pager.substring(0, pager.length() - 1);
                try {
                    book.getOrCreateNbt().copyFrom(StringNbtReader.parse("{title:\"\",author:\"ImageBook\",pages:['[" + loaderstr + "]']}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, book));
                NotificationSystem.notifications.add(new Notification("ImageBook", "Put image in book", 100));
            }
        }
    }

    public void loadImage(String imageurl, int size) {
        try {
            URL u = new URL(imageurl);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0");
            huc.connect();
            InputStream is = huc.getInputStream();
            BufferedImage loadedImage = ImageIO.read(is);
            double scale = (double) loadedImage.getWidth() / (double) size;
            imageToBuild = resize(loadedImage, (int) (loadedImage.getWidth() / scale), (int) (loadedImage.getHeight() / scale));
            NotificationSystem.notifications.add(new Notification("ImageLoader", "Loaded Image into memory", 100));
            huc.disconnect();
        } catch (Exception ignored) {
            NotificationSystem.notifications.add(new Notification("ImageLoader", "Failed to Loaded Image into memory", 100));
        }
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

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket p) {
            if (p.getMessage().getString().contains("Command set:")) {
                event.cancel();
            }
        }
    }
}
