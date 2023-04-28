package net.shadow.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.shadow.feature.ModuleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class SkinMixin {
    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    public void onGetSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if (ModuleRegistry.find("PersonHider").isEnabled()) {
            cir.setReturnValue(new Identifier("shadow", "skin.png"));
        }
    }
}