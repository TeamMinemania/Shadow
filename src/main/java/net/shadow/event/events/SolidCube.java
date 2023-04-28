//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface SolidCube extends Listener {
    void onSolidCube(SetSolidCube event);

    class SetSolidCube
            extends CancellableEvent<SolidCube> {
        @Override
        public void call(ArrayList<SolidCube> listeners) {
            for (SolidCube listener : listeners) {
                listener.onSolidCube(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<SolidCube> getThisType() {
            return SolidCube.class;
        }
    }
}
