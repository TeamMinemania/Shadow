package net.shadow.feature.module.movement;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public class Spider extends Module implements RenderListener, LeftClick {
    static BlockPos swinging;
    static int t = 0;

    public Spider() {
        super("Spider", "swing around like a spider", ModuleType.MOVEMENT);
    }

    public static double[] vecCalc(double px, double py, double pz, PlayerEntity me) {
        double dirx = me.getX() - px;
        double diry = me.getY() + me.getEyeHeight(me.getPose()) - py;
        double dirz = me.getZ() - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new double[]{yaw, pitch};
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(LeftClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
        Shadow.getEventSystem().remove(LeftClick.class, this);
    }

    @Override
    public void onUpdate() {
        if (swinging == null) return;
        double[] looks = vecCalc(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5, Shadow.c.player);
        Vec3d forces = Vec3d.fromPolar((float) looks[1], (float) looks[0]).normalize().multiply(0.4);
        Shadow.c.player.addVelocity(forces.x, forces.y, forces.z);
        Shadow.c.player.addVelocity(0, 0.0668500030517578, 0);
        if (Shadow.c.options.jumpKey.isPressed()) {
            swinging = null;
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (swinging == null || Shadow.c.player == null) return;
        Vec3d cringe = new Vec3d(swinging.getX(), swinging.getY(), swinging.getZ());
        Vec3d cringe2 = new Vec3d(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5);
        Entity entity = Shadow.c.player;
        Vec3d eSource = new Vec3d(MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevX, entity.getX()), MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevY, entity.getY()), MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevZ, entity.getZ()));
        RenderUtils.renderObject(cringe, new Vec3d(1, 1, 1), new Color(150, 150, 150, 150), matrix);
        RenderUtils.vector(eSource, cringe2, new Color(50, 50, 50, 255), matrix, 1);
    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        try {
            HitResult hit = Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
            swinging = new BlockPos(hit.getPos());
        } catch (Exception ignored) {
        }
    }
}
