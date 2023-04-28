package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface LeftClick extends Listener {
    void onLeftClick(LeftClickEvent event);

    class LeftClickEvent
            extends CancellableEvent<LeftClick> {
        @Override
        public void call(ArrayList<LeftClick> listeners) {
            for (LeftClick listener : listeners) {
                listener.onLeftClick(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<LeftClick> getThisType() {
            return LeftClick.class;
        }
    }
}
