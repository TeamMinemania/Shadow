package net.shadow.feature.configuration;

public class CustomValue<T> {
    private final String key;
    protected T value;

    public CustomValue(String v, T l) {
        this.key = v;
        this.value = l;
    }

    public T getThis() {
        return value;
    }

    public void setValue(Object value) {
        if (value.getClass() != this.getType()) return;
        this.value = (T) value;
    }

    public String getKey() {
        return key;
    }

    public Class<?> getType() {
        return value.getClass();
    }
}
