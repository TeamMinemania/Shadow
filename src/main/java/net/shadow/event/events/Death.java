package net.shadow.event.events;

import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface Death extends Listener {
    void onDeath();

    class DeathEvent extends Event<Death> {

        @Override
        public void call(ArrayList<Death> listeners) {
            for (Death listener : listeners)
                listener.onDeath();
        }

        @Override
        public Class<Death> getThisType() {
            return Death.class;
        }
    }
}
