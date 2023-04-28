package net.shadow.feature.module;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.font.FontRenderers;
import net.shadow.font.adapter.FontAdapter;
import net.shadow.plugin.Matrix4x4;
import net.shadow.plugin.Vector3D;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.util.Comparator;

public class NametagsModule extends Module implements RenderListener{

    static final Color GREEN = new Color(100, 255, 20);
    static final Color RED = new Color(255, 50, 20);

    public NametagsModule() {
        super("Nametags", "show entity names", ModuleType.RENDER);
    }

    public static boolean isOnScreen(Vec3d pos) {
        return pos != null && (pos.z > -1 && pos.z < 1);
    }

    public void render(MatrixStack stack, AbstractClientPlayerEntity entity, Text text) {
        String t = text.getString();

        Vec3d headPos = getInterpolatedEntityPosition(entity).add(0, entity.getHeight() + 0.3, 0);
        Vec3d a = getScreenSpaceCoordinate(headPos, stack);
        if (isOnScreen(a)) {
            Utils.runOnNextRender(() -> drawInternal(a, t, entity));
        }
    }

    void drawInternal(Vec3d screenPos, String text, AbstractClientPlayerEntity entity) {
        FontAdapter nameDrawer = FontRenderers.getRenderer();
        FontAdapter infoDrawer = FontRenderers.getCustomSize(12);
        double healthHeight = 2;
        double labelHeight = 2 + nameDrawer.getFontHeight() + infoDrawer.getFontHeight() + 2 + healthHeight + 2;
        int ping = -1;
        GameMode gamemode = null;
        PlayerListEntry ple = Shadow.c.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (ple != null) {
            gamemode = ple.getGameMode();
            ping = ple.getLatency();
        }
        String pingStr = (ping == 0 ? "?" : ping) + " ms";
        String gmString = "§cBot";
        if (gamemode != null) {
            switch (gamemode) {
                case ADVENTURE -> gmString = "Adventure";
                case CREATIVE -> gmString = "Creative";
                case SURVIVAL -> gmString = "Survival";
                case SPECTATOR -> gmString = "Spectator";
            }
        }
        MatrixStack stack1 = new MatrixStack();
        Vec3d actual = new Vec3d(screenPos.x, screenPos.y - labelHeight, screenPos.z);
        float width = nameDrawer.getStringWidth(text) + 4;
        width = Math.max(width, 60);

        RenderUtils.renderRoundedQuad(stack1, new Color(0, 0, 5, 100), actual.x - width / 2d, actual.y, actual.x + width / 2d, actual.y + labelHeight, 3);
        nameDrawer.drawString(stack1, text, actual.x + width / 2d - nameDrawer.getStringWidth(text) - 2, actual.y + 2, 0xFFFFFF);

        infoDrawer.drawString(stack1, gmString, actual.x + width / 2d - infoDrawer.getStringWidth(gmString) - 2, actual.y + 2 + nameDrawer.getFontHeight(), 0xAAAAAA);
        if (ping != -1) {
            infoDrawer.drawString(stack1, pingStr, actual.x - width / 2d + 2, actual.y + 2 + nameDrawer.getFontHeight(), 0xAAAAAA);
        }
        RenderUtils.renderRoundedQuad(stack1, new Color(60, 60, 60, 255), actual.x - width / 2d + 2, actual.y + labelHeight - 2 - healthHeight, actual.x + width / 2d - 2, actual.y + labelHeight - 2,
                healthHeight / 2d);
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPer = health / maxHealth;
        healthPer = MathHelper.clamp(healthPer, 0, 1);
        double drawTo = MathHelper.lerp(healthPer, actual.x - width / 2d + 2 + healthHeight, actual.x + width / 2d - 2);
        Color MID_END = RenderUtils.lerp(GREEN, RED, healthPer);
        RenderUtils.renderRoundedQuad(stack1, MID_END, actual.x - width / 2d + 2, actual.y + labelHeight - 2 - healthHeight, drawTo, actual.y + labelHeight - 2, healthHeight / 2d);
    }

    public static Vec3d getInterpolatedEntityPosition(Entity entity) {
        Vec3d a = entity.getPos();
        Vec3d b = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
        float p = Shadow.c.getTickDelta();
        return new Vec3d(MathHelper.lerp(p, b.x, a.x), MathHelper.lerp(p, b.y, a.y), MathHelper.lerp(p, b.z, a.z));
    }


    public static Vec3d getScreenSpaceCoordinate(Vec3d pos, MatrixStack stack) {
        Camera camera = Shadow.c.getEntityRenderDispatcher().camera;
        Matrix4f matrix = stack.peek().getPositionMatrix();
        double x = pos.x - camera.getPos().x;
        double y = pos.y - camera.getPos().y;
        double z = pos.z - camera.getPos().z;
        Vector4f vector4f = new Vector4f((float) x, (float) y, (float) z, 1.f);
        vector4f.transform(matrix);
        int displayHeight = Shadow.c.getWindow().getHeight();
        Vector3D screenCoords = new Vector3D();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Matrix4x4 matrix4x4Proj = Matrix4x4.copyFromColumnMajor(RenderSystem.getProjectionMatrix());//no more joml :)
        Matrix4x4 matrix4x4Model = Matrix4x4.copyFromColumnMajor(RenderSystem.getModelViewMatrix());//but I do the math myself now :( (heck math)
        matrix4x4Proj.mul(matrix4x4Model).project(vector4f.getX(), vector4f.getY(), vector4f.getZ(), viewport, screenCoords);
        return new Vec3d(screenCoords.x / Shadow.c.getWindow().getScaleFactor(), (displayHeight - screenCoords.y) / Shadow.c.getWindow().getScaleFactor(), screenCoords.z);
    }



    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (AbstractClientPlayerEntity player : Shadow.c.world.getPlayers().stream().sorted(Comparator.comparingDouble(value -> -value.getPos().distanceTo(Shadow.c.gameRenderer.getCamera().getPos()))).filter(abstractClientPlayerEntity -> !abstractClientPlayerEntity.equals(Shadow.c.player)).toList()) {
            render(matrix, player, player.getName());
        }
    }
}
