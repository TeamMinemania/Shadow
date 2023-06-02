/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.feature.items;


import java.util.concurrent.atomic.AtomicReference;


public class Option<T> {

    final String name;

    final T standardValueNullIfNothing;

    final Class<T> type;

    final AtomicReference<T> value = new AtomicReference<>();

    public AtomicReference<T> getValueRef() {
        return value;
    }

    public T getValue() {
        return getValueRef().get();
    }

    public void setValue(T value) {
        getValueRef().set(value);
    }

    public void accept(Object o) {
        getValueRef().set((T) o);
    }
}
