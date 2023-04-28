package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.PlayerUtils;

import java.util.Random;

public class DmSpammerModule extends Module {
    static int randomthistime = 0;
    final int ticks = 0;
    final SliderValue delay = this.config.create("Delay", 0, 5, 100, 0);
    final SliderValue randomticks = this.config.create("RandomTicks", 0, 0, 10, 0);
    final CustomValue<String> text = this.config.create("Text", "/msg {player} hi!");

    public DmSpammerModule() {
        super("DmSpammer", "spam peoples dms", ModuleType.CHAT);
    }

    @Override
    public void onEnable() {
        int a = (int) Math.round(randomticks.getThis() + 1);
        if (a < 0) a = 1;
        randomthistime = (int) ((delay.getThis() + new Random().nextInt(a)) - 1);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (ticks % randomthistime == 0) {
            String target = PlayerUtils.getRandomOnline();
            Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket(text.getThis().replace("{player}", target)));
            int a = (int) Math.round(randomticks.getThis() + 1);
            if (a < 0) a = 1;
            randomthistime = (int) (delay.getThis() + new Random().nextInt(Math.round(a)));
        }
    }

    @Override
    public void onRender() {

    }
}
