package net.shadow.event.events;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.shadow.event.base.CancellableEvent;
import net.shadow.event.base.Listener;

import java.util.ArrayList;
import java.util.List;

public interface ChatInput extends Listener {
    void onReceivedMessage(ChatInputEvent event);

    class ChatInputEvent
            extends CancellableEvent<ChatInput> {
        private final List<ChatHudLine<OrderedText>> chatLines;
        private Text component;

        public ChatInputEvent(Text component,
                              List<ChatHudLine<OrderedText>> visibleMessages) {
            this.component = component;
            chatLines = visibleMessages;
        }

        public Text getComponent() {
            return component;
        }

        public void setComponent(Text component) {
            this.component = component;
        }

        public List<ChatHudLine<OrderedText>> getChatLines() {
            return chatLines;
        }

        @Override
        public void call(ArrayList<ChatInput> listeners) {
            for (ChatInput listener : listeners) {
                listener.onReceivedMessage(this);

                if (isCancelled())
                    break;
            }
        }

        @Override
        public Class<ChatInput> getThisType() {
            return ChatInput.class;
        }
    }
}
