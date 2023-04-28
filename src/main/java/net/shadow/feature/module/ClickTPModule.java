package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.event.events.RenderListener;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.Random;

public class ClickTPModule extends Module implements RightClick, RenderListener, PacketOutput {
    private static BlockPos targeted = new BlockPos(0, 0, 0);
    final BooleanValue onlyctrl = this.config.create("OnlyWhenControl", false);
    final MultiValue mode = this.config.create("Mode", "Spliterator", "Spliterator", "Normal", "Tween");

    boolean catching;

    public ClickTPModule() {
        super("ClickTP", "teleport far by right click", ModuleType.MOVEMENT);
    }

    //0x150 is smrt funy monke
    private static int lengthTo(BlockPos p) {
        Vec3d v = new Vec3d(p.getX(), p.getY(), p.getZ());
        return (int) roundToN(v.distanceTo(Shadow.c.player.getPos()), 0);
    }

    public static double roundToN(double x, int n) {
        if (n == 0) return Math.floor(x);
        double factor = Math.pow(10, n);
        return Math.round(x * factor) / factor;
        
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RightClick.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(RightClick.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        BlockHitResult ray = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
        targeted = new BlockPos(ray.getBlockPos());
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRightClick(RightClickEvent event) {

        if (onlyctrl.getThis() && !Shadow.c.options.sprintKey.isPressed())
            return;

        if (mode.getThis().equalsIgnoreCase("Spliterator")) {
            new Thread(() -> {
                catching = true;
                BlockHitResult ray = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
                BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
                BlockPos d = new BlockPos(blockHitResult.getBlockPos());
                BlockPos dest = new BlockPos(d.getX() + 0.5, d.getY(), d.getZ() + 0.5);
                dest = dest.offset(Direction.UP, 1);
                Vec3d origin = Shadow.c.player.getPos();
                Vec3d destination = new Vec3d(dest.getX(), dest.getY(), dest.getZ());
    
                double distance = origin.distanceTo(destination);
                double steps = distance / 6;
                Vec3d delta = destination.subtract(origin).multiply(1/steps);
    
                catching = true;
                
    
                for(int i = 0; i < steps - 1; i++){
                    catching = false;
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * i), origin.y + (delta.y * i), origin.z + (delta.z * i), true));
                    catching = true;
                    Utils.sleep(10);
                }
                catching = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps - 1), origin.y + (delta.y * steps), origin.z + (delta.z * steps - 1), true));
                catching = true;
                Utils.sleep(10);
                catching = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps), origin.y + (delta.y * steps), origin.z + (delta.z * steps), true));
                catching = true;
                catching = false;
                Shadow.c.player.updatePosition(destination.getX(), destination.getY(), destination.getZ());
            }).start();
        } else if (mode.getThis().equalsIgnoreCase("Normal")) {
            BlockHitResult ray = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
            int rd = lengthTo(ray.getBlockPos());
            int raycastdistance = rd / 6;
            BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
            BlockPos d = new BlockPos(blockHitResult.getBlockPos());
            BlockPos dest = new BlockPos(d.getX() + 0.5, d.getY(), d.getZ() + 0.5);
            dest = dest.offset(Direction.UP, 1);
            Shadow.c.player.updatePosition(dest.getX(), dest.getY(), dest.getZ());
        } else {
            new Thread(() -> {
                BlockHitResult ray = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
                int rd = lengthTo(ray.getBlockPos());
                int raycastdistance = rd / 6;
                BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
                BlockPos d = new BlockPos(blockHitResult.getBlockPos());
                BlockPos dest = new BlockPos(d.getX() + 0.5, d.getY(), d.getZ() + 0.5);
                dest = dest.offset(Direction.UP, 1);
                int rdd = lengthTo(ray.getBlockPos());
                BlockPos destt = new BlockPos(blockHitResult.getBlockPos());
                Shadow.c.player.jump();
                ClientPlayerEntity player = Shadow.c.player;
                Vec3d playerpos = player.getPos();
                double xn = destt.getX() - playerpos.x;
                double yn = destt.getY() - playerpos.y;
                double zn = destt.getZ() - playerpos.z;
                double x = xn / rdd;
                double y = yn / rdd;
                double z = zn / rdd;
                for (int i = 0; i < rdd; i++) {
                    Shadow.c.player.updatePosition(player.getX() + x, player.getY() + y, player.getZ() + z);
                    try {
                        Thread.sleep(7);
                    } catch (Exception ignored) {
                    }
                    Shadow.c.player.setVelocity(0, 0, 0);
                }
            }).start();
        }

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        Vec3d vp = new Vec3d(targeted.getX(), targeted.getY(), targeted.getZ());
        RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(100, 100, 100, 255), matrix);
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            if(catching){
                event.cancel();
            }
        }
    }


}
