package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

public class BlockInteractModule extends Module implements RightClick {

    final SliderValue a = this.config.create("Amount", 500, 4, 4000, 1);
    final MultiValue mode = this.config.create("Mode", "PlaceThenBreak", "PlaceThenBreak", "Place");

    public BlockInteractModule() {
        super("BlockInteract", "interact with blocks differently", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
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
        BlockHitResult r = (BlockHitResult) Shadow.c.crosshairTarget;
        BlockPos p = r.getBlockPos();
        if (mode.getThis().equalsIgnoreCase("PlaceThenBreak")) {
            for (int i = 0; i < a.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, p, Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, p, Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, r));
            }
        } else {
            for (int i = 0; i < a.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, r));
            }
        }
    }
}
