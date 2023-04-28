package net.shadow.feature.base;

import net.shadow.Shadow;
import net.shadow.feature.configuration.ModuleSettings;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public abstract class Module {
    public final ModuleSettings config;
    private final String name;
    private final String desc;
    private final ModuleType moduleType;
    private boolean enabled = false;

    public Module(String nam, String des, ModuleType type) {
        this.name = nam;
        this.desc = des;
        this.moduleType = type;
        this.config = new ModuleSettings();
        this.config.create("Keybind", -1);
    }


    public ModuleType getModuleType() {
        return moduleType;
    }

    public String getName() {
        return name;
    }

    public String getVanityName() {
        return name;
    }

    public String getSpecial() {
        return "none";
    }

    public String getDescription() {
        return desc;
    }

    public abstract void onUpdate();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void onRender();

    public void toggle() {
        setEnabled(!enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!this.getName().equals("ClickGui") && Shadow.c.player != null && Shadow.c.world != null && !this.getName().equals("Wavedash")) {
            NotificationSystem.notifications.add(new Notification("Module", (this.enabled ? "En" : "Dis") + "abled " + this.getName(), 50));
            if (this.enabled) {
                Shadow.c.player.playSound(Shadow.ON, 1f, 1f);
            } else {
                Shadow.c.player.playSound(Shadow.OFF, 1f, 1f);
            }
        }
        if (this.enabled) this.onEnable();
        else this.onDisable();
    }

}
