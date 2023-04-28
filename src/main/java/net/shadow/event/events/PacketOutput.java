//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.event.events;

import net.minecraft.network.Packet;
import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface PacketOutput extends Listener {
    void onSentPacket(PacketOutputEvent event);

    class PacketOutputEvent
            extends CancellableEvent<PacketOutput> {
        private Packet<?> packet;

        public PacketOutputEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void setPacket(Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void call(ArrayList<PacketOutput> listeners) {
            for (PacketOutput listener : listeners) {
                listener.onSentPacket(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<PacketOutput> getThisType() {
            return PacketOutput.class;
        }
    }
}
