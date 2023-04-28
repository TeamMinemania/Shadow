package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class BoatLoaderModule extends Module {
    final SliderValue lockat = this.config.create("Lockat", 20, 20, 500, 0);

    public BoatLoaderModule() {
        super("BoatLoader", "load chunks with a boat", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Entity v = Shadow.c.player.getVehicle();
        if (v == null) {
            this.setEnabled(false);
            NotificationSystem.notifications.add(new Notification("BoatLoader", "You have to ride a boat!", 200));
            return;
        }
        if (v.getY() < lockat.getThis()) {
            v.updatePosition(v.getX(), v.getY() + 5, v.getZ());
            updatePosition(v);
        } else {
            for (int i = 0; i < 3; i++) {
                v.updatePosition(v.getX() + 5, v.getY(), v.getZ() + 5);
                updatePosition(v);
            }
        }
    }

    @Override
    public void onRender() {

    }


    private void updatePosition(Entity e) {
        Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(e));
    }
}
