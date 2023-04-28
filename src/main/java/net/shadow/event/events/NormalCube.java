package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface NormalCube extends Listener {
    void onIsNormalCube(NormalCubeEvent event);

    class NormalCubeEvent
            extends CancellableEvent<NormalCube> {
        @Override
        public void call(ArrayList<NormalCube> listeners) {
            for (NormalCube listener : listeners) {
                listener.onIsNormalCube(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<NormalCube> getThisType() {
            return NormalCube.class;
        }
    }
}
