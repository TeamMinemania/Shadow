package net.shadow.event.events;

import net.minecraft.network.Packet;
import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;


public interface PacketInput extends Listener {
    void onReceivedPacket(PacketInputEvent event);

    class PacketInputEvent
            extends CancellableEvent<PacketInput> {
        private final Packet<?> packet;

        public PacketInputEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        @Override
        public void call(ArrayList<PacketInput> listeners) {
            for (PacketInput listener : listeners) {
                listener.onReceivedPacket(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<PacketInput> getThisType() {
            return PacketInput.class;
        }
    }
}
