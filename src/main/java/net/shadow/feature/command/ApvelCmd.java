package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class ApvelCmd extends Command {

    private Double vx;
    private Double vy;
    private Double vz;

    public ApvelCmd() {
        super("apvel", "set your velocity");
    }

    @Override
    public void call(String[] args) {
        ClientPlayerEntity player = Shadow.c.player;

        if (args.length < 2) {
            ChatUtils.message("Please Provide three values for velocity");
            return;
        }

        Vec3d velocity = player.getVelocity();

        try {
            vx = Double.parseDouble(args[0]);
            vy = Double.parseDouble(args[1]);
            vz = Double.parseDouble(args[2]);
        } catch (Exception e) {
            ChatUtils.message("Please Provide three values for velocity");
        }


        try {
            Shadow.c.player.setVelocity(velocity.x + vx, velocity.y + vy, velocity.z + vz);
        } catch (Exception e) {
            ChatUtils.message("Please Provide three values for velocity");
        }
    }
}
