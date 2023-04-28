package net.shadow.feature.command;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class CheckCmdCmd extends Command {
    public CheckCmdCmd() {
        super("checkcmd", "check if cmdblocks are on");
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message("Checking command blocks");
        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(Shadow.c.player.getBlockPos().offset(Direction.DOWN, 1), "/", CommandBlockBlockEntity.Type.AUTO, false, false, false));
    }
}
