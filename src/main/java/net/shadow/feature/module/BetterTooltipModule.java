package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class BetterTooltipModule extends Module {
    private static boolean a;
    private static boolean b;
    private static boolean c;
    private static boolean d;
    private static boolean e;
    final BooleanValue sio = this.config.create("Size", true);
    final BooleanValue creator = this.config.create("Creator", true);
    final BooleanValue info = this.config.create("Info", true);
    final BooleanValue sptips = this.config.create("SpawnTips", true);

    public BetterTooltipModule() {
        super("Tooltips", "enhanced item tooltips", ModuleType.RENDER);
    }

    public static boolean ison(String val) {
        return switch (val) {
            case "size" -> a;
            case "creator" -> b;
            case "info" -> c;
            case "spawns" -> e;
            default -> false;
        };
    }

    public static boolean turnedOn() {
        return d;
    }

    @Override
    public void onEnable() {
        d = true;
    }

    @Override
    public void onDisable() {
        d = false;
    }

    @Override
    public void onUpdate() {
        a = sio.getThis();
        b = creator.getThis();
        c = info.getThis();
        e = sptips.getThis();
    }

    @Override
    public void onRender() {

    }
}
