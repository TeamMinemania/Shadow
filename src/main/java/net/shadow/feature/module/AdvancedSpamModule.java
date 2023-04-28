package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;

import java.util.Random;

public class AdvancedSpamModule extends Module {
    final CustomValue<String> message = this.config.create("Message", "/ad randomserver its good");
    final MultiValue rank = this.config.create("Rank", "Default", "Default", "VIP", "Pro", "Legend", "Patron");
    final MultiValue randomlevel = this.config.create("Randomness", "None", "None", "Small", "Medium", "Large", "Huge");
    long lastmessage = System.currentTimeMillis();

    public AdvancedSpamModule() {
        super("AdSpammer", "spam minehut ads", ModuleType.CHAT);
    }

    @Override
    public void onEnable() {
        lastmessage = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (lastmessage + delay() <= System.currentTimeMillis()) {
            Shadow.c.player.sendChatMessage(message.getThis());
            lastmessage = System.currentTimeMillis();
        }
    }


    @Override
    public void onRender() {

    }


    long delay() {
        long random = 0;
        long rango = 0;
        switch (randomlevel.getThis().toLowerCase()) {
            case "none" -> random = 500;
            case "small" -> random = 1000;
            case "medium" -> random = 4000;
            case "large" -> random = 10000;
            case "huge" -> random = 20000;
        }
        switch (rank.getThis().toLowerCase()) {
            case "default" -> rango = 480000;
            case "vip" -> rango = 420000;
            case "pro" -> rango = 300000;
            case "legend" -> rango = 180000;
            case "patron" -> rango = 60000;
        }
        return rango + new Random().nextInt((int) random) + 500;
    }
}
