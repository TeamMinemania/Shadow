package net.shadow.feature.configuration;

public class BooleanValue extends CustomValue<Boolean> {
    public BooleanValue(String key, boolean value) {
        super(key, value);
    }

    @Override
    public void setValue(Object value) {
        if (!(value instanceof Boolean)) return;
        this.value = (Boolean) value;
    }
}
