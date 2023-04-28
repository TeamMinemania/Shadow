package net.shadow.feature.command;

import net.minecraft.entity.Entity;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class EvclipCmd extends Command {
    public EvclipCmd() {
        super("evclip", "vertical clipping with entities!");
    }

    @Override
    public void call(String[] args) {
        int up = 0;
        try {
            up = Integer.parseInt(args[0]);
        } catch (Exception e) {
            ChatUtils.message("Invalid value");
        }

        if (!Shadow.c.player.hasVehicle()) {
            ChatUtils.message("Player is not riding an Entity");
            return;
        }

        Entity vehicle = Shadow.c.player.getVehicle();
        vehicle.updatePosition(vehicle.getX(), vehicle.getY() + up, vehicle.getZ());
        ChatUtils.message("Teleported entity");
    }
}
