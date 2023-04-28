package net.shadow.feature.command;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;

public class AsConsoleCmd extends Command {
    public AsConsoleCmd() {
        super("asconsole", "run a command using cmdblocks, spoofing as console, this needs op no shit");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"/op " + Shadow.c.player.getGameProfile().getName()});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        ItemStack console = new ItemStack(Items.CHAIN_COMMAND_BLOCK, 1);
        String command = String.join(" ", args);
        ItemStack b4 = Shadow.c.player.getMainHandStack();
        BlockHitResult b = (BlockHitResult) Shadow.c.crosshairTarget;
        BlockPos cbp = b.getBlockPos();
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, console));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(cbp, command, CommandBlockBlockEntity.Type.REDSTONE, false, false, false));
        Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(cbp, command, CommandBlockBlockEntity.Type.REDSTONE, false, false, true));
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            Shadow.c.interactionManager.attackBlock(cbp, Direction.DOWN);
        }).start();
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, b4));
    }
}
