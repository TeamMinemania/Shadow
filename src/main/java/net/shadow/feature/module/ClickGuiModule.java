package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class ClickGuiModule extends Module {

    private static boolean faster = false;
    private static boolean nobg = true;
    final BooleanValue enhance = this.config.create("Faster", false);
    final BooleanValue anim = this.config.create("Animation", true);

    public ClickGuiModule() {
        super("ClickGui", "da click gui", ModuleType.RENDER);
        this.config.get("Keybind").setValue(344);
    }

    public static boolean fast() {
        return faster;
    }

    public static boolean bg() {
        return nobg;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onUpdate() {
        faster = enhance.getThis();
        nobg = anim.getThis();
        Shadow.c.setScreen(net.shadow.alickgui.ClickGUI.instance());
        toggle();
    }
}
