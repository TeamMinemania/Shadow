package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.Translate;
import net.shadow.utils.ChatUtils;

public class ChatTranslatorModule extends Module implements PacketInput, PacketOutput {
    private static final Translate translator = new Translate();
    final CustomValue<String> lang = this.config.create("From-To", "fr-en");

    public ChatTranslatorModule() {
        super("ChatTranslator", "translate to and from languages", ModuleType.CHAT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket packet) {
            event.cancel();
            String chatmessagepacket = packet.getChatMessage();
            new Thread(() -> {
                try {
                    String[] splitline = String.join(" ", lang.getThis()).split("-");
                    String translated = translate(chatmessagepacket, splitline[1], splitline[0]);
                    if (!translated.isEmpty() && !translated.isBlank()) {
                        Shadow.c.getNetworkHandler().getConnection().send(new ChatMessageC2SPacket(translated));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket packet) {
            new Thread(() -> {
                try {
                    String[] splitline = String.join(" ", lang.getThis()).split("-");
                    String translated = translate(packet.getMessage().getString(), splitline[0], splitline[1]);
                    if (!translated.isEmpty() && !translated.isBlank()) {
                        ChatUtils.message(translated);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    private String translate(String text, String from, String to) {
        String translated = translator.translate(text, from, to);
        if (translated == null || text.contains("Shadow"))
            return text;
        return translated;
    }
}