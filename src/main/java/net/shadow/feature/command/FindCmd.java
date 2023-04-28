package net.shadow.feature.command;

import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class FindCmd extends Command implements PacketInput {
    public FindCmd() {
        super("find", "find da players");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length > 1) {
            ChatUtils.message("Please use the format >find <player>");
            return;
        }

        new Thread(() -> {
            Shadow.getEventSystem().add(PacketInput.class, this);
            BlockPos b = PlayerUtils.locate(args[0]);
            ChatUtils.message(b.getX() + " " + b.getY() + " " + b.getZ());
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("copy")) {
                    Shadow.c.keyboard.setClipboard(b.getX() + " " + b.getY() + " " + b.getZ());
                }
            }
        }).start();
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof OpenWrittenBookS2CPacket) {
            event.cancel();
            Shadow.getEventSystem().remove(PacketInput.class, this);
        }
    }
}

