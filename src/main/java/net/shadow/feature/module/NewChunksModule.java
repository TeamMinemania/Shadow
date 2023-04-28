package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NewChunksModule extends Module implements PacketInput, RenderListener {

    private static final Direction[] skipDirs = new Direction[]{Direction.DOWN, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};


    private final Set<ChunkPos> newChunks = Collections.synchronizedSet(new HashSet<>());
    private final Set<ChunkPos> oldChunks = Collections.synchronizedSet(new HashSet<>());

    public NewChunksModule() {
        super("NewChunks", "da test module", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
        newChunks.clear();
        oldChunks.clear();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    //this is completely taken from bleachhack lol
    //needed to try my best updating shit to 1.18 tho
    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        Direction[] searchDirs = new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.UP};

        if (event.getPacket() instanceof ChunkDeltaUpdateS2CPacket packet) {

            packet.visitUpdates((pos, state) -> {
                if (!state.getFluidState().isEmpty() && !state.getFluidState().isStill()) {
                    ChunkPos chunkPos = new ChunkPos(pos);

                    for (Direction dir : searchDirs) {
                        if (Shadow.c.world.getBlockState(pos.offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos)) {
                            newChunks.add(chunkPos);
                            return;
                        }
                    }
                }
            });
        } else if (event.getPacket() instanceof BlockUpdateS2CPacket packet) {

            if (!packet.getState().getFluidState().isEmpty() && !packet.getState().getFluidState().isStill()) {
                ChunkPos chunkPos = new ChunkPos(packet.getPos());

                for (Direction dir : searchDirs) {
                    if (Shadow.c.world.getBlockState(packet.getPos().offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos)) {
                        newChunks.add(chunkPos);
                        return;
                    }
                }
            }
        } else if (event.getPacket() instanceof ChunkDataS2CPacket packet && Shadow.c.world != null) {

            ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());

            if (!newChunks.contains(pos) && Shadow.c.world.getChunkManager().getChunk(packet.getX(), packet.getZ()) == null) {
                WorldChunk chunk = new WorldChunk(Shadow.c.world, pos);
                chunk.loadFromPacket(null, new NbtCompound(), packet.getChunkData().getBlockEntities(packet.getX(), packet.getZ()));

                for (int x = 0; x < 16; x++) {
                    for (int y = Shadow.c.world.getBottomY(); y < Shadow.c.world.getTopY(); y++) {
                        for (int z = 0; z < 16; z++) {
                            FluidState fluid = chunk.getFluidState(x, y, z);

                            if (!fluid.isEmpty() && !fluid.isStill()) {
                                oldChunks.add(pos);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        synchronized (newChunks) {
            for (ChunkPos c : newChunks) {
                if (Shadow.c.getCameraEntity().getBlockPos().isWithinDistance(c.getStartPos(), 1024)) {
                    RenderUtils.renderObject(new Vec3d(c.getStartX(), 0, c.getStartZ()), new Vec3d(16, 1, 16), new Color(0, 0, 255, 255), matrix);
                }
            }
        }
    }
}
