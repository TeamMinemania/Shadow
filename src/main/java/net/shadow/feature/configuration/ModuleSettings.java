package net.shadow.feature.configuration;

import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;


public class ModuleSettings {
    final List<CustomValue<?>> config = new ArrayList<>();

    private void addProxy(CustomValue<?> v) {
        config.add(v);
    }

    public <T> CustomValue<T> create(String key, T value) {
        CustomValue<T> custom = new CustomValue<>(key, value);
        addProxy(custom);
        return custom;
    }

    public SliderValue create(String key, double value, double min, double max, int p) {
        SliderValue slider = new SliderValue(key, MathHelper.clamp(value, min, max), min, max, p);
        addProxy(slider);
        return slider;
    }

    public BooleanValue create(String key, boolean defa) {
        BooleanValue bool = new BooleanValue(key, defa);
        addProxy(bool);
        return bool;
    }

    public MultiValue create(String key, String value, String... possible) {
        MultiValue ev = new MultiValue(key, value, possible);
        addProxy(ev);
        return ev;
    }

    public CustomValue<?> get(String key) {
        for (CustomValue<?> customValue : config) {
            if (customValue.getKey().equalsIgnoreCase(key)) return customValue;
        }
        return null;
    }

    public List<CustomValue<?>> returnThis() {
        return config;
    }
}
