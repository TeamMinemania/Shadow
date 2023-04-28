package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class ForceTeleportCmd extends Command {

    private static int tpid = 0;

    public ForceTeleportCmd() {
        super("forcetp", "tries to bypass speed limits and teleport the player far");
    }

    @Override
    public void call(String[] args) {
        BlockPos pos = argsToPos(args);
        if (pos == null) {
            ChatUtils.message("Please use the format >forceteleport <x> <y> <z>");
            return;
        }
        Vec3d np = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        Vec3d ppos = Shadow.c.player.getPos();
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(np.x, np.y, np.z, Shadow.c.player.isOnGround()));
        Shadow.c.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(++tpid));
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 1850, ppos.z, Shadow.c.player.isOnGround()));
        Shadow.c.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(++tpid));
        Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(np.x, np.y, np.z, Shadow.c.player.isOnGround()));
        Shadow.c.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(tpid));
    }

    private BlockPos argsToPos(String... xyz) {
        BlockPos playerPos = new BlockPos(Shadow.c.player.getPos());
        int[] player =
                new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()};
        int[] pos = new int[3];
        try {
            for (int i = 0; i < 3; i++)

                if (xyz[i].equals("~"))
                    pos[i] = player[i];
                else if (xyz[i].startsWith("~"))
                    pos[i] = player[i] + Integer.parseInt(xyz[i].substring(1));
                else
                    pos[i] = Integer.parseInt(xyz[i]);

            return new BlockPos(pos[0], pos[1], pos[2]);
        } catch (Exception e) {
            return null;
        }
    }
}
