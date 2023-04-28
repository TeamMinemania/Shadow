package net.shadow.alickgui.element.impl.config;

import net.shadow.alickgui.element.Element;
import net.shadow.feature.configuration.CustomValue;

public abstract class ConfigBase<T extends CustomValue<?>> extends Element {
    final T configValue;

    public ConfigBase(double x, double y, double width, double height, T configValue) {
        super(x, y, width, height);
        this.configValue = configValue;
    }

    public CustomValue<?> getConfigValue() {
        return configValue;
    }
}
