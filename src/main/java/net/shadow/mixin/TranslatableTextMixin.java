package net.shadow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.shadow.feature.module.AntiCrashModule;

@Mixin(TranslatableText.class)
public class TranslatableTextMixin {
    @Inject(method="getArg", at=@At("HEAD"), cancellable=true)
    private void onGetArg(int index, CallbackInfoReturnable<StringVisitable> cir){
        if(AntiCrashModule.shouldBlockPoof() && index < 0){
            cir.setReturnValue(StringVisitable.plain("among-us"));
        }
    }
}
