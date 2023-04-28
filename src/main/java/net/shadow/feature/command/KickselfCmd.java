package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class KickselfCmd extends Command {
    public KickselfCmd() {
        super("kickself", "bypass antilog and kick yourself");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"quit", "chars", "packet", "self", "spam", "packets"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please use the format >kickself <quit/chars/packet/self/spam/packets>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "spam":
                for (int i = 0; i < 50; i++) {
                    Shadow.c.player.sendChatMessage("/");
                }
                break;

            case "quit":
                Shadow.c.world.disconnect();
                break;

            case "chars":
                Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("\u00a7"));
                break;

            case "packet":
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(3.1e7, 100, 3.1e7, false));
                break;

            case "self":
                PlayerInteractEntityC2SPacket h = PlayerInteractEntityC2SPacket.attack(Shadow.c.player, false);
                Shadow.c.player.networkHandler.sendPacket(h);
                break;

            case "packets":
                for (int i = 0; i < 5000; i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                }
                break;
        }

    }
}
