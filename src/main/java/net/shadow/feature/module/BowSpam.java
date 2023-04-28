package net.shadow.feature.module;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class BowSpam extends Module {
    final SliderValue useitemtime = this.config.create("Delay", 2, 3, 20, 1);

    public BowSpam() {
        super("BowSpam", "spam bow", ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (Shadow.c.player.getMainHandStack().getItem().equals(Items.BOW) && Shadow.c.player.isUsingItem()) {
            if (Shadow.c.player.getItemUseTime() < useitemtime.getThis()) return;
            Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, Shadow.c.player.getBlockPos(), Shadow.c.player.getHorizontalFacing()));
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Shadow.c.player.getActiveHand()));
            Shadow.c.player.stopUsingItem();
        }
    }

    @Override
    public void onRender() {

    }
}
