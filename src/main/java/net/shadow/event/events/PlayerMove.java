package net.shadow.event.events;

import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;
import net.shadow.inter.IClientPlayerEntity;

import java.util.ArrayList;

public interface PlayerMove extends Listener {
    void onPlayerMove(IClientPlayerEntity player);

    class PlayerMoveEvent extends Event<PlayerMove> {
        private final IClientPlayerEntity player;

        public PlayerMoveEvent(IClientPlayerEntity player) {
            this.player = player;
        }

        @Override
        public void call(ArrayList<PlayerMove> listeners) {
            for (PlayerMove listener : listeners)
                listener.onPlayerMove(player);
        }

        @Override
        public Class<PlayerMove> getThisType() {
            return PlayerMove.class;
        }
    }
}
