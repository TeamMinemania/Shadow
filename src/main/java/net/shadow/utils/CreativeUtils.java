package net.shadow.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CreativeUtils {
    public static void give(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (!Shadow.c.player.getInventory().getStack(i).isEmpty())
                continue;

            Shadow.c.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + i, stack));
            break;
        }
    }

    public static NbtCompound parse(String nbt) {
        try {
            return StringNbtReader.parse(nbt);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void setSlot(int slot, ItemStack stack) {
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot, stack));
    }

    public static int[] getIntsFromUser(String username) {
        String uuidstring;
        try {
            uuidstring = getUUID(username);
        } catch (IOException e) {
            return null;
        }
        UUID player = UUID.fromString(uuidstring.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        byte[] bytes = getBytesFromUUID(player);
        byte[] i1 = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
        byte[] i2 = new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]};
        byte[] i3 = new byte[]{bytes[8], bytes[9], bytes[10], bytes[11]};
        byte[] i4 = new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]};
        int ii1 = toInt(i1);
        int ii2 = toInt(i2);
        int ii3 = toInt(i3);
        int ii4 = toInt(i4);
        return new int[]{ii1, ii2, ii3, ii4};
    }

    public static int[] getIntsFromUUID(String UUIDString) {
        UUID player = UUID.fromString(UUIDString.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        byte[] bytes = getBytesFromUUID(player);
        byte[] i1 = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
        byte[] i2 = new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]};
        byte[] i3 = new byte[]{bytes[8], bytes[9], bytes[10], bytes[11]};
        byte[] i4 = new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]};
        int ii1 = toInt(i1);
        int ii2 = toInt(i2);
        int ii3 = toInt(i3);
        int ii4 = toInt(i4);
        return new int[]{ii1, ii2, ii3, ii4};
    }

    private static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static String getUUID(String username) throws IOException {
        URL profileURL =
                URI.create("https://api.mojang.com/users/profiles/minecraft/")
                        .resolve(URLEncoder.encode(username, StandardCharsets.UTF_8)).toURL();

        try (InputStream profileInputStream = profileURL.openStream()) {
            // {"name":"<username>","id":"<UUID>"}

            JsonObject profileJson = new Gson().fromJson(
                    IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                    JsonObject.class);

            return profileJson.get("id").getAsString();
        }
    }

    public static String getUsername(String uuid) {

        try{
            URL profileURL = URI.create("https://sessionserver.mojang.com/session/minecraft/profile/")
            .resolve(URLEncoder.encode(uuid.replace("-", ""), StandardCharsets.UTF_8)).toURL();
            // {"name":"<username>","id":"<UUID>"}

            InputStream profileInputStream = profileURL.openStream();

            JsonObject profileJson = new Gson().fromJson(
                    IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                    JsonObject.class);

            return profileJson.get("name").getAsString();
        }catch(Exception e){
            return "bobby";
        }
    }
    public static String getUUID2(String username) {

        try{
            URL profileURL = URI.create("https://api.mojang.com/users/profiles/minecraft/")
            .resolve(URLEncoder.encode(username, StandardCharsets.UTF_8)).toURL();
            // {"name":"<username>","id":"<UUID>"}

            InputStream profileInputStream = profileURL.openStream();

            JsonObject profileJson = new Gson().fromJson(
                    IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                    JsonObject.class);

            return profileJson.get("id").getAsString().replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" );
        }catch(Exception e){
            return "1-2-3-4-5";
        }
    }

    private static int toInt(byte[] bytes) {
        int ret = 0;
        for (int i = 0; i < 4; i++) {
            ret <<= 8;
            ret |= (int) bytes[i] & 0xFF;
        }
        return ret;
    }
}
