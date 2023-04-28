package net.shadow.event.events;

import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;
import java.util.Objects;

public interface ChatOutput extends Listener {
    void onSentMessage(ChatOutputEvent event);

    class ChatOutputEvent
            extends CancellableEvent<ChatOutput> {
        private final String originalMessage;
        private String message;
        private boolean ismodded;

        public ChatOutputEvent(String message) {
            this.message = Objects.requireNonNull(message);
            this.ismodded = false;
            originalMessage = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
            this.ismodded = true;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public boolean isModified() {
            return ismodded;
        }

        @Override
        public void call(ArrayList<ChatOutput> listeners) {
            for (ChatOutput listener : listeners) {
                listener.onSentMessage(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<ChatOutput> getThisType() {
            return ChatOutput.class;
        }
    }
}
