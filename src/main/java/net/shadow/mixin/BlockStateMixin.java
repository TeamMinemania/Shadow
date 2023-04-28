package net.shadow.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.shadow.Shadow;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.NormalCube.NormalCubeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public class BlockStateMixin extends State<Block, BlockState> {
    private BlockStateMixin(Shadow shadow, Block object,
                            ImmutableMap<Property<?>, Comparable<?>> immutableMap,
                            MapCodec<BlockState> mapCodec) {
        super(object, immutableMap, mapCodec);
    }

    @Inject(at = {@At("TAIL")}, method = {"isFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"}, cancellable = true)
    private void onIsFullCube(BlockView world, BlockPos pos,
                              CallbackInfoReturnable<Boolean> cir) {
        NormalCubeEvent event = new NormalCubeEvent();
        EventHandler.call(event);

        cir.setReturnValue(cir.getReturnValue() && !event.isCancelled());
    }
}
