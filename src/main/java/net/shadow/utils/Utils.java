package net.shadow.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Utils {
    static final List<Runnable> nextTickRunners = new ArrayList<>();

    public static boolean parseFromCompoundString(String compound, int index) {
        String[] split = compound.split(":");
        if (split.length + 1 < index) return false;
        try {
            return Boolean.parseBoolean(split[index]);
        } catch (Exception e) {
            return false;
        }

    }

    public static void moveTo(Vec3d destination){
        Vec3d origin = Shadow.c.player.getPos();
        double distance = origin.distanceTo(destination);
        double steps = distance / 6;
        Vec3d delta = destination.subtract(origin).multiply(1/steps);
        
        for(int i = 0; i < steps - 1; i++){
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * i), origin.y + (delta.y * i), origin.z + (delta.z * i), true));
            Utils.sleep(10);
        }
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps - 1), origin.y + (delta.y * steps), origin.z + (delta.z * steps - 1), true));
        Utils.sleep(10);
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps), origin.y + (delta.y * steps), origin.z + (delta.z * steps), true));
        Shadow.c.player.updatePosition(destination.getX(), destination.getY(), destination.getZ());
    }


    public static String[] getPlayersFromWorld(){
        return Objects.requireNonNull(Shadow.c.player.networkHandler).getPlayerList().stream().map(real -> real.getProfile().getName()).toList().toArray(String[]::new);
    }

    public static Vec3d RenderCameraStart() {
        ClientPlayerEntity player = Shadow.c.player;
        Camera camera = Shadow.c.gameRenderer.getCamera();
        float f = 0.017453292F;
        float pi = (float) Math.PI;

        float f1 = (float) Math.cos(-player.getYaw() * f - pi);
        float f2 = (float) Math.sin(-player.getYaw() * f - pi);
        float f3 = (float) -Math.cos(-player.getPitch() * f);
        float f4 = (float) Math.sin(-player.getPitch() * f);

        return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String rndStr(int size) {
        StringBuilder buf = new StringBuilder();
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            buf.append(chars[r.nextInt(chars.length)]);
        }
        return buf.toString();
    }

    public static void drawLine(Vec3d destination, String particlename, int count) {
        double counterx = Shadow.c.player.getX();
        double countery = Shadow.c.player.getY();
        double counterz = Shadow.c.player.getZ();
        double xn = destination.x - counterx;
        double yn = destination.y - countery;
        double zn = destination.z - counterz;
        for (int i = 0; i < lengthTo(destination); i++) {
            Shadow.c.player.sendChatMessage("/particle " + particlename + " " + counterx + " " + countery + " " + counterz + " 0 0 0 0.001 " + count + " force");
            counterx += (xn / lengthTo(destination));
            countery += (yn / lengthTo(destination));
            counterz += (zn / lengthTo(destination));
        }
    }

    private static int lengthTo(Vec3d v) {
        return (int) roundToN(v.distanceTo(Shadow.c.player.getPos()), 0);
    }

    public static double roundToN(double x, int n) {
        if (n == 0) return Math.floor(x);
        double factor = Math.pow(10, n);
        return Math.round(x * factor) / factor;
    }

    public static String getRandomContent() {
        String[] nouns = new String[]{"bird", "clock", "boy", "plastic", "duck", "teacher", "old lady", "professor", "hamster", "dog"};
        String[] verbs = new String[]{"kicked", "ran", "flew", "dodged", "sliced", "rolled", "died", "breathed", "slept", "killed"};
        String[] adjectives = new String[]{"beautiful", "lazy", "professional", "lovely", "dumb", "rough", "soft", "hot", "vibrating", "slimy"};
        String[] adverbs = new String[]{"slowly", "elegantly", "precisely", "quickly", "sadly", "humbly", "proudly", "shockingly", "calmly", "passionately"};
        String[] preposition = new String[]{"down", "into", "up", "on", "upon", "below", "above", "through", "across", "towards"};
        return "The " + adjectives[random()] + " " + nouns[random()] + " " + adverbs[random()] + " " + verbs[random()] + " because some " + nouns[random()] + " " + adverbs[random()] + " " + verbs[random()] + " " + preposition[random()] + " a " + adjectives[random()] + " " + nouns[random()] + " which, became a " + adjectives[random()] + ", " + adjectives[random()] + " " + nouns[random()] + ".";
    }

    public static String getRandomTitle() {
        String[] nouns = new String[]{"bird", "clock", "boy", "plastic", "duck", "teacher", "old lady", "professor", "hamster", "dog"};
        String[] adjectives = new String[]{"beautiful", "lazy", "professional", "lovely", "dumb", "rough", "soft", "hot", "vibrating", "slimy"};
        return "The " + adjectives[random()] + " tale of a " + nouns[random()];
    }

    private static int random() {
        return (int) Math.floor(Math.random() * 10);
    }

    public static boolean ranged(int a, int b, int c) {
        int d = Math.abs(a - b);
        return d <= c;
    }

    public static double randomEulerRotation() {
        return (Math.random() - 0.5) * Math.PI * 2;
    }

    public static void loop(int amount, Runnable r) {
        for (int i = 0; i < amount; i++) {
            r.run();
        }
    }

    public static void runOnNextRender(Runnable r) {
        nextTickRunners.add(r);
    }

    public static void render() {
        for (Runnable nextTickRunner : nextTickRunners) {
            nextTickRunner.run();
        }
        nextTickRunners.clear();
    }

    public static void twice(Runnable r) {
        try {
            r.run();
            r.run();
        } catch (Exception ignored) {
        }
    }

    public static Vec3d relativeToAbsolute(Vec3d absRootPos, Vec2f rotation, Vec3d relative) {
        double xOffset = relative.x;
        double yOffset = relative.y;
        double zOffset = relative.z;
        float rot = 0.017453292F;
        float f = MathHelper.cos((rotation.y + 90.0F) * rot);
        float g = MathHelper.sin((rotation.y + 90.0F) * rot);
        float h = MathHelper.cos(-rotation.x * rot);
        float i = MathHelper.sin(-rotation.x * rot);
        float j = MathHelper.cos((-rotation.x + 90.0F) * rot);
        float k = MathHelper.sin((-rotation.x + 90.0F) * rot);
        Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
        Vec3d vec3d3 = new Vec3d(f * j, k, g * j);
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0D);
        double d = vec3d2.x * zOffset + vec3d3.x * yOffset + vec3d4.x * xOffset;
        double e = vec3d2.y * zOffset + vec3d3.y * yOffset + vec3d4.y * xOffset;
        double l = vec3d2.z * zOffset + vec3d3.z * yOffset + vec3d4.z * xOffset;
        return new Vec3d(absRootPos.x + d, absRootPos.y + e, absRootPos.z + l);
    }

    public static double getMouseX() {
        return Shadow.c.mouse.getX() / Shadow.c.getWindow().getScaleFactor();
    }

    public static double getMouseY() {
        return Shadow.c.mouse.getY() / Shadow.c.getWindow().getScaleFactor();
    }
}
