package net.shadow.feature.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NukerModule extends Module implements RenderListener {


    final List<BlockPos> renders = new ArrayList<>();
    final MultiValue mode = this.config.create("Mode", "Packet", "Packet", "Interaction", "AntiSwear", "Griefing", "Illustrate");
    int ticks = 0;

    public NukerModule() {
        super("Nuker", "Automatically destroy", ModuleType.GRIEF);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 5 == 0) {
            renders.clear();
        }
        if (Shadow.c.player.networkHandler == null || Shadow.c.player == null || Shadow.c.world == null) return;
        if (mode.getThis().equalsIgnoreCase("Interaction")) {
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(Shadow.c.player.getPos()) > Shadow.c.interactionManager.getReachDistance() - 1 || Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.WATER || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                            continue;
                        renders.add(pos);
                        Shadow.c.interactionManager.attackBlock(pos, Direction.DOWN);
                    }
        } else if (mode.getThis().equalsIgnoreCase("packet")) {
            if (ticks % 2 != 0) return;
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(Shadow.c.player.getPos()) > Shadow.c.interactionManager.getReachDistance() - 1 || Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.WATER || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                            continue;
                        renders.add(pos);
                        Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.DOWN));
                        Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.DOWN));
                    }
        } else if (mode.getThis().equalsIgnoreCase("antiswear")) {
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(Shadow.c.player.getPos()) > Shadow.c.interactionManager.getReachDistance() - 1 || Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.WATER || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                            continue;
                        renders.add(pos);
                        Shadow.c.getNetworkHandler().sendPacket(new UpdateSignC2SPacket(pos, "FUCK", "BITCH", "CUNT", "FUCK"));
                    }
        } else if (mode.getThis().equalsIgnoreCase("griefing")) {
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(Shadow.c.player.getPos()) > Shadow.c.interactionManager.getReachDistance() - 1 || Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.WATER || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.LAVA || !isGood(Shadow.c.world.getBlockState(pos).getBlock()))
                            continue;
                        renders.add(pos);
                        Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.DOWN));
                        Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.DOWN));
                    }
        } else {
            if (ticks % 10 == 0) {
                new Thread(() -> {
                    for (int x = -7; x < 8; x++)
                        for (int y = -7; y < 8; y++)
                            for (int z = -7; z < 8; z++) {
                                BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                                if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(Shadow.c.player.getPos()) > Shadow.c.interactionManager.getReachDistance() - 1 || Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.WATER || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.LAVA || Shadow.c.world.getBlockState(pos).getBlock().equals(Blocks.RED_CONCRETE) || isNotSurfaceBlock(pos))
                                    continue;
                                renders.add(pos);
                                Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.DOWN));
                                Utils.sleep(100);
                            }
                }).start();
            }
        }
    }

    private boolean isNotSurfaceBlock(BlockPos pos) {
        if (Shadow.c.world.getBlockState(pos.offset(Direction.UP, 1)).isAir()) return false;
        if (Shadow.c.world.getBlockState(pos.offset(Direction.DOWN, 1)).isAir()) return false;
        if (Shadow.c.world.getBlockState(pos.offset(Direction.NORTH, 1)).isAir()) return false;
        if (Shadow.c.world.getBlockState(pos.offset(Direction.EAST, 1)).isAir()) return false;
        if (Shadow.c.world.getBlockState(pos.offset(Direction.SOUTH, 1)).isAir()) return false;
        return !Shadow.c.world.getBlockState(pos.offset(Direction.WEST, 1)).isAir();
    }

    private boolean isGood(Block b) {
        if (b.equals(Blocks.BEETROOTS)) return true;
        if (b.equals(Blocks.CARROTS)) return true;
        if (b.equals(Blocks.DEAD_BUSH)) return true;
        if (b.equals(Blocks.FERN)) return true;
        if (b.equals(Blocks.GRASS)) return true;
        if (b.equals(Blocks.TALL_GRASS)) return true;
        if (b.equals(Blocks.HANGING_ROOTS)) return true;
        if (b.equals(Blocks.LILY_PAD)) return true;
        if (b.equals(Blocks.MELON_STEM)) return true;
        if (b.equals(Blocks.PUMPKIN_STEM)) return true;
        if (b.equals(Blocks.SUGAR_CANE)) return true;
        if (b.equals(Blocks.SWEET_BERRY_BUSH)) return true;
        if (b.equals(Blocks.WHEAT)) return true;
        return b.equals(Blocks.VINE);
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
