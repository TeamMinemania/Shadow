package net.shadow.feature.module.chat;

import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class Spammer extends Module {
    static World w;
    final SliderValue speed = this.config.create("Delay", 15, 1, 40, 1);
    final CustomValue<String> message = this.config.create("Message", "hello world");
    final BooleanValue aas = this.config.create("Bypass", false);
    double timer;
    boolean doubletapenable = false;

    public Spammer() {
        super("Spammer", "Spam the chat", ModuleType.CHAT);
    }

    @Override
    public void onEnable() {
        timer = -1;
        w = Shadow.c.player.clientWorld;
        if (doubletapenable) {
            doubletapenable = false;
        } else {
            doubletapenable = true;
            ChatUtils.message("Press again to toggle the module");
            this.setEnabled(false);
        }
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
        if (timer > -1) {
            timer--;
            return;
        }
        String rchars = String.valueOf((int) (Math.random() * 8000));
        rchars += String.valueOf((int) (Math.random() * 8000));
        rchars += String.valueOf((int) (Math.random() * 8000));
        rchars += String.valueOf((int) (Math.random() * 8000));

        String msg;
        if (aas.getThis()) {
            msg = message.getThis() + " [" + rchars + "]";
        } else {
            msg = message.getThis();
        }
        Shadow.c.player.sendChatMessage(msg);
        timer = speed.getThis();
    }


    @Override
    public void onRender() {

    }
}
