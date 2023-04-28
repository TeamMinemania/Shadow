package net.shadow.feature.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import net.shadow.utils.Utils;

import java.util.List;

public class UnstuckCmd extends Command {
    public UnstuckCmd() {
        super("unstuck", "fixes the players movement");
    }

    @Override
    public void call(String[] args) {
        Utils.moveTo(new Vec3d(Shadow.c.player.getX() , Shadow.c.player.getY() + 100, Shadow.c.player.getZ()));
    }
}
