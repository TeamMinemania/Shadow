package net.shadow.feature.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InteracterModule extends Module implements RenderListener {

    final List<BlockPos> renders = new ArrayList<>();

    public InteracterModule() {
        super("Interacter", "interact with blocks", ModuleType.WORLD);
    }

    private static boolean checkblockpos(BlockPos pos) {
        Block block = Shadow.c.world.getBlockState(pos).getBlock();
        if (block == Blocks.CHEST) return false;
        if (block == Blocks.TRAPPED_CHEST) return false;
        if (block == Blocks.SHULKER_BOX) return false;
        if (block == Blocks.BARREL) return false;
        if (block == Blocks.ENDER_CHEST) return false;
        if (block == Blocks.DAYLIGHT_DETECTOR) return false;
        if (block == Blocks.LEVER) return false;
        if (block == Blocks.OAK_TRAPDOOR) return false;
        if (block == Blocks.OAK_DOOR) return false;
        if (block == Blocks.OAK_FENCE_GATE) return false;
        if (block == Blocks.BIRCH_TRAPDOOR) return false;
        if (block == Blocks.BIRCH_DOOR) return false;
        if (block == Blocks.BIRCH_FENCE_GATE) return false;
        if (block == Blocks.DARK_OAK_TRAPDOOR) return false;
        if (block == Blocks.DARK_OAK_DOOR) return false;
        if (block == Blocks.DARK_OAK_FENCE_GATE) return false;
        if (block == Blocks.JUNGLE_TRAPDOOR) return false;
        if (block == Blocks.JUNGLE_DOOR) return false;
        return block != Blocks.JUNGLE_FENCE_GATE;
    }

    @Override
    public void onEnable() {
        for (int x = -5; x < 6; x++)
            for (int y = -5; y < 6; y++)
                for (int z = -5; z < 6; z++) {
                    BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                    if (checkblockpos(pos)) continue;
                    renders.add(pos);

                }
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
        renders.clear();
    }

    @Override
    public void onUpdate() {
        for (BlockPos hit : renders) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, hit, false)));
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (BlockPos render : renders) {
            Vec3d vp = new Vec3d(render.getX(), render.getY(), render.getZ());
            RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        }
    }
}
