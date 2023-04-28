package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface RightClick extends Listener {
    void onRightClick(RightClickEvent event);

    class RightClickEvent
            extends CancellableEvent<RightClick> {
        @Override
        public void call(ArrayList<RightClick> listeners) {
            for (RightClick listener : listeners) {
                listener.onRightClick(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<RightClick> getThisType() {
            return RightClick.class;
        }
    }
}
