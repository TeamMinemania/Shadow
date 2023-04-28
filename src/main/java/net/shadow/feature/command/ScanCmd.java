package net.shadow.feature.command;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

public class ScanCmd extends Command implements PacketInput {
    public ScanCmd() {
        super("scan", "scan for f3+i ranges");
    }

    @Override
    public void call(String[] args) {
        new Thread(() -> {
            Shadow.getEventSystem().add(PacketInput.class, this);
            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                Shadow.c.player.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(0, i));
            }
            Utils.sleep(5000);
            Shadow.getEventSystem().remove(PacketInput.class, this);
        }).start();


    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof NbtQueryResponseS2CPacket p) {
            NbtCompound g = p.getNbt();
            ChatUtils.message(g.asString());
        }
    }
}
