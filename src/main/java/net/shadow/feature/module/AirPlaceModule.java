package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.shadow.Shadow;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AirPlaceModule extends Module implements RightClick {
    public AirPlaceModule() {
        super("AirPlace", "place blocks midair", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RightClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RightClick.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRightClick(RightClickEvent event) {
        try {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
            Shadow.c.player.swingHand(Hand.MAIN_HAND);
        } catch (Exception ignored) {

        }
    }
}
