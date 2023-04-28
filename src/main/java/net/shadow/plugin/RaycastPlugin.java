package net.shadow.plugin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;

public class RaycastPlugin {
    public static void raycast(BlockPos destination) {
        ItemStack item = new ItemStack(Items.COW_SPAWN_EGG, 1);
        ItemStack before = Shadow.c.player.getMainHandStack();
        try {
            item.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:end_crystal\",ShowBottom:0b,BeamTarget:{X:" + destination.getX() + ",Y:" + destination.getY() + ",Z:" + destination.getZ() + "}}}"));
        } catch (Exception ignored) {
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, item));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.0, 0.0, 0.0), Direction.UP, Shadow.c.player.getBlockPos().offset(Direction.DOWN, 2), false)));
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
    }
}
