package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.ChatUtils;

public class BoatFuckModule extends Module {


    final MultiValue mode = this.config.create("Mode", "Break", "Break", "Fling", "Spin");
    Entity boat = null;

    public BoatFuckModule() {
        super("BoatFuck", "Boat Exploits", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        if (!Shadow.c.player.hasVehicle()) {
            setEnabled(false);
            ChatUtils.message("Please Ride an Entity");
        }
        boat = Shadow.c.player.getVehicle();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        switch (mode.getThis().toLowerCase()) {
            case "break" -> boat.noClip = true;
            case "fling" -> {
                boat.updatePosition(boat.getX(), boat.getY() + 10, boat.getZ());
                Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(boat));
                boat.setVelocity(0, 0, 0);
            }
            case "spin" -> {
                boat.setYaw((float) Math.random() * 100);
                Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(boat));
            }
        }
    }

    @Override
    public void onRender() {

    }
}
