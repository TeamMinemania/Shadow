package net.shadow.feature.module.grief;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public class WorldPainter extends Module implements RightClick, RenderListener {
    static double raycastdist = 12;
    static BlockPos a = new BlockPos(0, 0, 0);
    static double slide = 3;
    private String material;

    public WorldPainter() {
        super("WorldPainter", "alternate annihilator", ModuleType.GRIEF);
    }

    public static void raycast(double double_2) {
        if (Shadow.c.options.sprintKey.isPressed()) {
            raycastdist += double_2 * 2;
        }
        if (Shadow.c.options.playerListKey.isPressed()) {
            slide += double_2;
        }
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RightClick.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RightClick.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        this.material = Registry.ITEM.getId(Shadow.c.player.getMainHandStack().getItem()).getPath();
        if(material == "lava_bucket")  material = "lava"; else if(material == "water_bucket") material = "water";
        if (Shadow.c.options.useKey.isPressed()) {
            int x1 = a.getX() + (int) Math.round(slide);
            int y1 = a.getY() + (int) Math.round(slide);
            int z1 = a.getZ() + (int) Math.round(slide);
            int x2 = a.getX() - (int) Math.round(slide);
            int y2 = a.getY() - (int) Math.round(slide);
            int z2 = a.getZ() - (int) Math.round(slide);
            Shadow.c.player.sendChatMessage("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " minecraft:" + material);
        }
        if (Shadow.c.options.attackKey.isPressed()) {
            int x1 = a.getX() + (int) Math.round(slide);
            int y1 = a.getY() + (int) Math.round(slide);
            int z1 = a.getZ() + (int) Math.round(slide);
            int x2 = a.getX() - (int) Math.round(slide);
            int y2 = a.getY() - (int) Math.round(slide);
            int z2 = a.getZ() - (int) Math.round(slide);
            Shadow.c.player.sendChatMessage("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " minecraft:air");
        }
        Vec3d forward = Vec3d.fromPolar(Shadow.c.player.getPitch(), Shadow.c.player.getYaw()).normalize();
        a = Shadow.c.player.getBlockPos().add(forward.x * raycastdist, forward.y * raycastdist, forward.z * raycastdist);
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRightClick(RightClickEvent event) {
        int x1 = a.getX() + (int) Math.round(slide);
        int y1 = a.getY() + (int) Math.round(slide);
        int z1 = a.getZ() + (int) Math.round(slide);
        int x2 = a.getX() - (int) Math.round(slide);
        int y2 = a.getY() - (int) Math.round(slide);
        int z2 = a.getZ() - (int) Math.round(slide);
        Shadow.c.player.sendChatMessage("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " minecraft:" + material);
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        RenderUtils.renderObject(new Vec3d(a.getX() - (slide / 2), a.getY() - (slide / 2), a.getZ() - (slide / 2)), new Vec3d(slide * 2, slide * 2, slide * 2), new Color(100, 100, 100, 175), matrix);
        RenderUtils.vector(Shadow.c.player.getPos(), new Vec3d(a.getX() + (slide / 2), a.getY() + (slide / 2), a.getZ() + (slide / 2)), new Color(25, 25, 25, 255), matrix, 1);
    }
}
