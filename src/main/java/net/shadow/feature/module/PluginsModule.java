package net.shadow.feature.module;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import joptsimple.internal.Strings;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginsModule extends Module implements PacketInput {
    public PluginsModule() {
        super("Plugins", "get dem plugins", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
            List<String> plugins = new ArrayList<>();
            Suggestions all = packet.getSuggestions();
            if (all == null) {
                ChatUtils.message("Invalid Packet, retrying...");
                Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
                return;
            }
            for (Suggestion i : all.getList()) {
                String[] cmd = i.getText().split(":");
                if (cmd.length > 1) {
                    String name = cmd[0].replace("/", "");
                    if (!plugins.contains(name)) plugins.add(name);
                }
            }
            Collections.sort(plugins);

            ChatUtils.message("Plugins [" + plugins.size() + "]" + " : " + Strings.join(plugins.toArray(new String[0]), ", "));
            setEnabled(false);
        }
    }
}
