package net.shadow.event.events;

import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface KeyboardListener extends Listener {
    void onKeypress(int key, int action);

    class KeyboardEvent extends Event<KeyboardListener> {
        private final int key;
        private final int action;

        public KeyboardEvent(int key, int action) {
            this.key = key;
            this.action = action;
        }

        @Override
        public void call(ArrayList<KeyboardListener> listeners) {
            for (KeyboardListener listener : listeners)
                listener.onKeypress(key, action);
        }

        @Override
        public Class<KeyboardListener> getThisType() {
            return KeyboardListener.class;
        }
    }
}
