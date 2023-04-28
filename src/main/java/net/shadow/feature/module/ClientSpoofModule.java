package net.shadow.feature.module;

import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;

public class ClientSpoofModule extends Module {
    public static String brandstr = "Vanilla";
    final CustomValue<String> brand = this.config.create("Brand", "Vanilla");

    public ClientSpoofModule() {
        super("ClientSpoof", "change the client brand", ModuleType.EXPLOIT);
    }

    public static String getBrand() {
        return brandstr;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        brandstr = brand.getThis();
    }

    @Override
    public void onRender() {

    }
}
