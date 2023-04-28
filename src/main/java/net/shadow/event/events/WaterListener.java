//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.event.events;

import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface WaterListener extends Listener {
    void onIsPlayerInWater(IsPlayerInWaterEvent event);

    class IsPlayerInWaterEvent
            extends Event<WaterListener> {
        private final boolean normallyInWater;
        private boolean inWater;

        public IsPlayerInWaterEvent(boolean inWater) {
            this.inWater = inWater;
            normallyInWater = inWater;
        }

        public boolean isInWater() {
            return inWater;
        }

        public void setInWater(boolean inWater) {
            this.inWater = inWater;
        }

        public boolean isNormallyInWater() {
            return normallyInWater;
        }

        @Override
        public void call(ArrayList<WaterListener> listeners) {
            for (WaterListener listener : listeners)
                listener.onIsPlayerInWater(this);
        }

        @Override
        public Class<WaterListener> getThisType() {
            return WaterListener.class;
        }
    }
}
