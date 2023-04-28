package net.shadow.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class PlayerUtils {
    public static String completeName(String smallname) {
        String result = "none";
        for (PlayerListEntry info : Shadow.c.player.networkHandler.getPlayerList()) {
            String name = info.getProfile().getName();

            if (name.toLowerCase().startsWith(smallname.toLowerCase())) {
                result = name;
            }
        }
        if (result.equals("none")) return smallname;
        return result;
    }

    public static boolean isOnline(String fullname) {
        for (PlayerListEntry info : Shadow.c.player.networkHandler.getPlayerList()) {
            String name = info.getProfile().getName();


            if (name.equalsIgnoreCase(fullname)) {
                return true;
            }
        }
        return false;
    }

    public static Entity getEntity(String viewName) {
        if (viewName != null && !viewName.isEmpty()) {
            return StreamSupport
                    .stream(Shadow.c.world.getEntities().spliterator(), false)
                    .filter(e -> e instanceof LivingEntity)
                    .filter(e -> !e.isRemoved() && ((LivingEntity) e).getHealth() > 0)
                    .filter(e -> viewName.equalsIgnoreCase(e.getName().getString()))
                    .min(Comparator
                            .comparingDouble(e -> Shadow.c.player.squaredDistanceTo(e)))
                    .orElse(null);
        }
        return null;
    }

    public static void tweenTo(BlockPos dest) {
        int rd = lengthTo(dest);
        int raycastdistance = rd / 7;
        Shadow.c.player.jump();
        ClientPlayerEntity player = Shadow.c.player;
        Vec3d playerpos = player.getPos();
        double xn = dest.getX() - playerpos.x;
        double yn = dest.getY() - playerpos.y;
        double zn = dest.getZ() - playerpos.z;
        double x = xn / raycastdistance;
        double y = yn / raycastdistance;
        double z = zn / raycastdistance;
        for (int i = 0; i < raycastdistance; i++) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + x, player.getY() + y, player.getZ() + z, true));
        }
        player.updatePosition(dest.getX(), dest.getY(), dest.getZ());
    }

    private static int lengthTo(BlockPos p) {
        Vec3d v = new Vec3d(p.getX(), p.getY(), p.getZ());
        return (int) roundToN(v.distanceTo(Shadow.c.player.getPos()), 0);
    }

    private static double roundToN(double x, int n) {
        if (n == 0) return Math.floor(x);
        double factor = Math.pow(10, n);
        return Math.round(x * factor) / factor;
    }

    public static BlockPos locate(String player) {
        try {
            ItemStack hbefore = Shadow.c.player.getMainHandStack();
            ItemStack s = new ItemStack(Items.WRITTEN_BOOK, 1);
            s.setNbt(StringNbtReader.parse("{pages:['{\"text\":\"nbtexample\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"nbt\":\"Pos\",\"entity\":\"" + player + "\"}]}}'],title:\"\",author:\"\",resolved:0b}"));
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, s));
            Thread.sleep(500);
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            Thread.sleep(200);
            ItemStack ustack = Shadow.c.player.getMainHandStack();
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, hbefore));
            String nbt = ustack.getNbt().asString();
            nbt = nbt.replace("{author:\"\",pages:['{\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"", "");
            nbt = nbt.replace("\"}},\"text\":\"nbtexample\"}'],resolved:1b,title:\"\"}", ""); //[-202.69999998807907d,99.0d,67.66826379181784d]
            nbt = nbt.replace("\u00a7", ""); //why
            if (!Pattern.matches(".*d,.*d,.*d.*", nbt)) {
                return null;
            }
            nbt = nbt.replace("[", "");
            nbt = nbt.replace("]", "");
            nbt = nbt.replace("d", "");
            String[] coords = nbt.split(",");
            double _x = Double.parseDouble(coords[0]);
            double _y = Double.parseDouble(coords[1]);
            double _z = Double.parseDouble(coords[2]);
            return new BlockPos(_x, _y, _z);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRandomOnline() {
        Collection<PlayerListEntry> players = Shadow.c.player.networkHandler.getPlayerList();
        PlayerListEntry picked = players.toArray(PlayerListEntry[]::new)[new Random().nextInt(players.size())];
        String name = picked.getProfile().getName();
        if (isBadName(name)) {
            return getRandomOnline();
        }
        return name;
    }

    public static String[] getPlayerList() {
        Collection<PlayerListEntry> players = Shadow.c.player.networkHandler.getPlayerList();
        PlayerListEntry[] play = players.toArray(PlayerListEntry[]::new);
        String[] playerz = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerz[i] = play[i].getProfile().getName();
        }

        return playerz;
    }

    public static Entity getEntityFromWorldByViewName(String viewName) {
        if (viewName != null && !viewName.isEmpty()) {

            return StreamSupport
                    .stream(Shadow.c.world.getEntities().spliterator(), false)
                    .filter(e -> e instanceof LivingEntity)
                    .filter(e -> !e.isRemoved() && ((LivingEntity) e).getHealth() > 0)
                    .filter(e -> e != Shadow.c.player)
                    .filter(e -> viewName.equalsIgnoreCase(e.getName().getString()))
                    .min(Comparator
                            .comparingDouble(e -> Shadow.c.player.squaredDistanceTo(e)))
                    .orElse(null);
        }
        return null;
    }


    static boolean isBadName(String name) {
        String[] badnames = new String[]{
                "MOD",
                "ADMIN",
                "MEDIA",
                "OWNER",
                "DEV",
                "JRMOD",
                "HELPER"
        };
        for (String bad : badnames) {
            if (name.toLowerCase().contains(bad.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
