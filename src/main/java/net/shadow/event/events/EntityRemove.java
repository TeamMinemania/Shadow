package net.shadow.event.events;

import net.minecraft.entity.Entity;
import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface EntityRemove extends Listener {
    void onEntityRemove(Entity removed);

    class EntityRemoveEvent extends Event<EntityRemove> {
        private final Entity removed;

        public EntityRemoveEvent(Entity removed) {
            this.removed = removed;
        }

        @Override
        public void call(ArrayList<EntityRemove> listeners) {
            for (EntityRemove listener : listeners)
                listener.onEntityRemove(removed);
        }

        @Override
        public Class<EntityRemove> getThisType() {
            return EntityRemove.class;
        }
    }
}
