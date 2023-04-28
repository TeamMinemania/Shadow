package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface MiddleClick extends Listener {
    void onMiddleClick(MiddleClickEvent event);

    class MiddleClickEvent
            extends CancellableEvent<MiddleClick> {
        @Override
        public void call(ArrayList<MiddleClick> listeners) {
            for (MiddleClick listener : listeners) {
                listener.onMiddleClick(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<MiddleClick> getThisType() {
            return MiddleClick.class;
        }
    }
}
