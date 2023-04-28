package net.shadow.event.base;

import java.util.ArrayList;

public abstract class Event<T extends Listener> {
    public abstract void call(ArrayList<T> listeners);

    public abstract Class<T> getThisType();
}

