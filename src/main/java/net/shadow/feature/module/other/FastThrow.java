package net.shadow.feature.module.other;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class FastThrow extends Module {

    final SliderValue v = this.config.create("Amount", 10, 1, 10000, 1);

    public FastThrow() {
        super("FastThrow", "many interact", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (!Shadow.c.options.useKey.isPressed())
            return;


        for (int i = 0; i < v.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        }
    }

    @Override
    public void onRender() {

    }
}
