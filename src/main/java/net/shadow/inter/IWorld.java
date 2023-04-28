package net.shadow.inter;

import net.minecraft.world.chunk.BlockEntityTickInvoker;

import java.util.List;

public interface IWorld {
    List<BlockEntityTickInvoker> getBlockEntityTickers();
}
