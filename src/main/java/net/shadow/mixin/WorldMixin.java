package net.shadow.mixin;

import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.shadow.inter.IWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
    @Shadow
    @Final
    protected List<BlockEntityTickInvoker> blockEntityTickers;

    @Override
    public List<BlockEntityTickInvoker> getBlockEntityTickers() {
        return blockEntityTickers;
    }
}
