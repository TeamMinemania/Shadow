//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.event.base;

import net.shadow.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class EventHandler {
    private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap =
            new HashMap<>();


    public static <L extends Listener, E extends Event<L>> void call(E event) {
        EventHandler events = Shadow.getEventSystem();
        if (events != null) {
            events.doevent(event);
        }
    }

    private <L extends Listener, E extends Event<L>> void doevent(E event) {
        try {
            Class<L> type = event.getThisType();
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners == null || listeners.isEmpty())
                return;

            ArrayList<L> listeners2 = new ArrayList<>(listeners);
            listeners2.removeIf(Objects::isNull);

            event.call(listeners2);

        } catch (Throwable ignored) {
        }
    }

    public <L extends Listener> void add(Class<L> type, L listener) {
        try {
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners == null) {
                listeners = new ArrayList<>(List.of(listener));
                listenerMap.put(type, listeners);
                return;
            }

            listeners.add(listener);

        } catch (Throwable ignored) {
        }
    }

    public <L extends Listener> void remove(Class<L> type, L listener) {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners != null)
                listeners.remove(listener);

        } catch (Throwable ignored) {
        }
    }
}
