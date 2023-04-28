package net.shadow.mixin;

import net.minecraft.entity.passive.PufferfishEntity;
import net.shadow.feature.module.AntiCrashModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PufferfishEntity.class)
public class PufferfishMixin {
    @Inject(method = "getPuffState", at = @At("TAIL"), cancellable = true)
    public void onGetPuffState(CallbackInfoReturnable<Integer> cir) {
        if (AntiCrashModule.getPuffers() && (cir.getReturnValue() > 3 || cir.getReturnValue() < 0))
            cir.setReturnValue(0);
    }
}
