package net.shadow.plugin;

import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;

import java.util.HashMap;
import java.util.Map;

public class Keybinds {
    public static final Map<Module, Keybind> binds = new HashMap<>();

    public static void init() {
        for (Module m : ModuleRegistry.getAll()) {
            if (m.config.get("Keybind").getThis().equals(-1.0)) return;
            binds.put(m, new Keybind(Integer.parseInt(String.valueOf(m.config.get("Keybind").getThis()))));
        }
    }

    public static void call() {
        for (Module m : binds.keySet().toArray(new Module[0])) {
            Keybind kb = binds.get(m);
            if (kb.isPressed()) m.toggle();
        }
    }


    public static void reload() {
        binds.clear();
        init();
    }
}
