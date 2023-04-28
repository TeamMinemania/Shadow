package net.shadow.event.events;

import net.minecraft.entity.Entity;
import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface EntitySpawn extends Listener {
    void onEntitySpawn(Entity Spawnd);

    class EntitySpawnEvent extends Event<EntitySpawn> {
        private final Entity Spawnd;

        public EntitySpawnEvent(Entity Spawnd) {
            this.Spawnd = Spawnd;
        }

        @Override
        public void call(ArrayList<EntitySpawn> listeners) {
            for (EntitySpawn listener : listeners)
                listener.onEntitySpawn(Spawnd);
        }

        @Override
        public Class<EntitySpawn> getThisType() {
            return EntitySpawn.class;
        }
    }
}
