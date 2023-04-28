package net.shadow.event.events;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface RenderListener extends Listener {
    void onRender(float partialTicks, MatrixStack matrix);

    class RenderEvent extends Event<RenderListener> {
        private final float partialTicks;
        private final MatrixStack matrix;

        public RenderEvent(float partialTicks, MatrixStack matrix) {
            this.partialTicks = partialTicks;
            this.matrix = matrix;
        }

        @Override
        public void call(ArrayList<RenderListener> listeners) {
            for (RenderListener listener : listeners)
                listener.onRender(partialTicks, matrix);
        }

        @Override
        public Class<RenderListener> getThisType() {
            return RenderListener.class;
        }
    }
}
