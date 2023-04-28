package net.shadow.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.shadow.feature.module.AntiCrashModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AreaEffectCloudEntity.class)
public class AreaEffectCloudEntityMixin {
    @Inject(method = "getRadius", at = @At("HEAD"), cancellable = true)
    void onGetRadius(CallbackInfoReturnable<Float> cir) {
        if (AntiCrashModule.shouldCapAoes()) {
            if (cir.getReturnValueF() > 15F) {
                cir.setReturnValue(15F);
            }
        }
    }
}
