//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.mixin;

import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.SolidCube.SetSolidCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionMixin {
    @Inject(at = {@At("HEAD")},
            method = {"markClosed(Lnet/minecraft/util/math/BlockPos;)V"},
            cancellable = true)
    private void onMarkClosed(BlockPos pos, CallbackInfo ci) {
        SetSolidCube event = new SetSolidCube();
        EventHandler.call(event);

        if (event.isCancelled())
            ci.cancel();
    }
}
