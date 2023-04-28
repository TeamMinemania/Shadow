package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class ClipCmd extends Command {
    public ClipCmd() {
        super("clip", "clips your player forward");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"5"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            ChatUtils.message("Please use the format >clip <amount>");
            return;
        }



        try {
            double blocks = Double.parseDouble(args[0]);
            ClientPlayerEntity player = Shadow.c.player;

            Vec3d forward = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
            player.updatePosition(player.getX() + forward.x * blocks, player.getY() + forward.y * blocks, player.getZ() + forward.z * blocks);
        } catch (Exception ignored) {
            ChatUtils.message("Please use the format >clip <amount>");
        }
    }
}

