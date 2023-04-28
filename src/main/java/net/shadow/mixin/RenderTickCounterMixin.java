package net.shadow.mixin;

import net.minecraft.client.render.RenderTickCounter;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.world.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {

    @Shadow
    public float lastFrameDuration;

    @Shadow
    public float tickDelta;

    @Shadow
    private long prevTimeMillis;

    @Final
    @Shadow
    private float tickTime;

    @Inject(at = @At("HEAD"), method = "beginRenderTick", cancellable = true)
    public void beginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
        if (Timer.getState()) {
            this.lastFrameDuration = (long_1 - this.prevTimeMillis) / this.tickTime;
            lastFrameDuration *= Timer.getTime();
            this.prevTimeMillis = long_1;
            this.tickDelta += this.lastFrameDuration;
        }
        if (ModuleRegistry.find("ChunkRender").isEnabled()) {
            this.lastFrameDuration = (long_1 - this.prevTimeMillis) / this.tickTime;
            lastFrameDuration *= Double.parseDouble(ModuleRegistry.find("ChunkRender").getSpecial());
            this.prevTimeMillis = long_1;
            this.tickDelta += this.lastFrameDuration;
        }
    }
}
