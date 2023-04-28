package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class BetterChatModule extends Module {
    static boolean cbutton;
    static boolean ichat;
    static boolean chatutline;
    static boolean customcolor;
    final BooleanValue infchat = this.config.create("InfChat", false);
    final BooleanValue chatcolor = this.config.create("CustomColor", false);
    BooleanValue crashbutton = this.config.create("HudButtons", false);

    public BetterChatModule() {
        super("EnhancedChat", "better chat", ModuleType.CHAT);
    }

    public static boolean infChat() {
        return ichat;
    }

    public static boolean crashButton() {
        return cbutton;
    }

    public static boolean customColor() {
        return customcolor;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        cbutton = infchat.getThis();
        ichat = infchat.getThis();
        customcolor = chatcolor.getThis();
    }

    @Override
    public void onRender() {
    }
}
