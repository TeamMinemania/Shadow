package net.shadow.feature.module.world;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;

public class SignWriter extends Module {
    final MultiValue mode = this.config.create("Mode", "AutoSign", "AutoSign", "Overload", "Crash", "Lag", "Dupe");
    final CustomValue<String> text1 = this.config.create("Text1", "");
    final CustomValue<String> text3 = this.config.create("Text3", "");
    final CustomValue<String> text2 = this.config.create("Text2", "");
    final CustomValue<String> text4 = this.config.create("Text4", "");

    public SignWriter() {
        super("SignWriter", "write signs", ModuleType.WORLD);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public String getSpecial() {
        return switch (mode.getThis().toLowerCase()) {
            case "autosign" -> text1.getThis() + ":" + text2.getThis() + ":" + text3.getThis() + ":" + text4.getThis();
            case "overload" -> "overload";
            case "crash" -> "crash";
            case "lag" -> "render";
            case "dupe" -> "dupe";
            default -> "none";
        };
    }

    @Override
    public void onRender() {

    }
}
