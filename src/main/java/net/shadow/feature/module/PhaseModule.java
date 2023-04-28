package net.shadow.feature.module;

import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class PhaseModule extends Module {

    final MultiValue m = this.config.create("Mode", "Shadow", "Shadow", "Basic", "Sneak", "Bypass");
    int ticks;

    public PhaseModule() {
        super("Phase", "noclip but workign", ModuleType.MOVEMENT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }


    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        ticks = 0;
    }

    @Override
    public void onUpdate() {
        if (m.getThis().equalsIgnoreCase("sneak")) {
            if (Shadow.c.options.sneakKey.isPressed() && Shadow.c.player.verticalCollision) {
                for (int i = Shadow.c.player.getBlockY() - 1; i > 0; i--) {
                    if (Shadow.c.world.getBlockState(Shadow.c.player.getBlockPos().subtract(new Vec3i(0, Shadow.c.player.getBlockY() - i, 0))).getBlock() == Blocks.AIR && Shadow.c.world.getBlockState(Shadow.c.player.getBlockPos().subtract(new Vec3i(0, Shadow.c.player.getBlockY() - i - 1, 0))).getBlock() == Blocks.AIR) {
                        Shadow.c.player.updatePosition(Shadow.c.player.getX(), i, Shadow.c.player.getZ());
                        break;
                    }
                }
            }
        }
        if (!Shadow.c.player.horizontalCollision) return;
        Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();
        ticks++;
        if (m.getThis().equalsIgnoreCase("shadow")) {
            if (ticks % 10 != 0) return;
            clip(3);
        } else if (m.getThis().equalsIgnoreCase("basic")) {
            Shadow.c.player.updatePosition(Shadow.c.player.getX() + forward.x * 3, Shadow.c.player.getY(), Shadow.c.player.getZ() + forward.z * 3);
        } else if (m.getThis().equalsIgnoreCase("bypass")) {
            ClientPlayerEntity player = Shadow.c.player;
            Vec3i vec3i = Shadow.c.player.getHorizontalFacing().getVector();
            player.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
            player.updatePosition(player.getX() + vec3i.getX() * 2, player.getY(), player.getZ() + vec3i.getZ() * 2);
            Shadow.c.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), false));
        }
    }

    @Override
    public void onRender() {

    }

    private void clip(double blocks) {
        Vec3d pos = Shadow.c.player.getPos();
        Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();
        float oldy = Shadow.c.player.getYaw();
        float oldp = Shadow.c.player.getPitch();
        sendPosition(pos.x, pos.y + 9, pos.z, true);
        sendPosition(pos.x, pos.y + 18, pos.z, true);
        sendPosition(pos.x, pos.y + 27, pos.z, true);
        sendPosition(pos.x, pos.y + 36, pos.z, true);
        sendPosition(pos.x, pos.y + 45, pos.z, true);
        sendPosition(pos.x, pos.y + 54, pos.z, true);
        sendPosition(pos.x, pos.y + 63, pos.z, true);
        sendPosition(pos.x + forward.x * blocks, Shadow.c.player.getY(), pos.z + forward.z * blocks, true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY() - 8.9, Shadow.c.player.getZ(), true);
        sendPosition(Shadow.c.player.getX(), Shadow.c.player.getY(), Shadow.c.player.getZ(), true);
        Shadow.c.player.setYaw(oldy);
        Shadow.c.player.setPitch(oldp);
    }

    private void sendPosition(double x, double y, double z, boolean onGround) {
        Shadow.c.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }
}

