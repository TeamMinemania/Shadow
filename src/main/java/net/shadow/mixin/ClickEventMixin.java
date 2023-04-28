package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.shadow.feature.ModuleRegistry;
import net.shadow.utils.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Screen.class)
public class ClickEventMixin {
    @Inject(method = "sendMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    public void sendMessage(String message, boolean toHud, CallbackInfo ci) {
        if (!toHud && ModuleRegistry.find("NoClickEvent").isEnabled()) {
            ci.cancel();
            ChatUtils.message("ClickEvent Blocked: \"" + message + "\"");
        }
    }
}
