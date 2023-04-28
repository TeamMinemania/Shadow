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

public class ChestVoiderModule extends Module{

    public ChestVoiderModule() {
        super("ChestVoider", "void chestss", ModuleType.WORLD);
    }


    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }
}
