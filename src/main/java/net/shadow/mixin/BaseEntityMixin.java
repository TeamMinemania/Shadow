package net.shadow.mixin;

import net.minecraft.entity.Entity;
import net.shadow.inter.GConf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class BaseEntityMixin {
    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void getTargetArea(CallbackInfoReturnable<Float> cri) {
        double a = GConf.boxSize;
        if (a != 0 && GConf.doHitBox) cri.setReturnValue((float) a);
    }
}