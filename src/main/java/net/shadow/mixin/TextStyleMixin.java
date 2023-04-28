package net.shadow.mixin;

import net.minecraft.text.Style;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.render.ViewChanges;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class TextStyleMixin {
    @Inject(method = "isObfuscated", at = @At("HEAD"), cancellable = true)
    public void isObfuscatedOverwrite(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleRegistry.find("ViewChanges").isEnabled() && ViewChanges.noscramble()) cir.setReturnValue(false);
    }
}