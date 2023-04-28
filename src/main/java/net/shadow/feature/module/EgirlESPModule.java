package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class EgirlESPModule extends Module implements PacketOutput {

    public EgirlESPModule() {
        super("EgirlESP", "e grill esp", ModuleType.RENDER);
    }

    public static boolean isLivingEntityInstanceOfPlayerEntity(Entity e) {
        return e.isPlayer();
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            Shadow.c.player.playSound(Shadow.MOAN, 1f, 1f);
        }
    }
}
