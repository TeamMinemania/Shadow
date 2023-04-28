package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class HClipCmd extends Command {
    public HClipCmd() {
        super("hclip", "clip forward");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            ChatUtils.message("Please use the format >hclip <length>");
            return;
        }

        try {
            double brik = Double.parseDouble(args[0]);
            Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize();

            if (Shadow.c.player.getAbilities().creativeMode) {
                Shadow.c.player.updatePosition(Shadow.c.player.getX() + forward.x * brik, Shadow.c.player.getY(), Shadow.c.player.getZ() + forward.z * brik);

            } else {
                clip(brik);
            }

        } catch (NumberFormatException ignored) {
            sendErrorMsg();
        }
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

    private void sendErrorMsg() {
        ChatUtils.message("Give a value.");
    }

    private void sendPosition(double x, double y, double z, boolean onGround) {
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }
}
