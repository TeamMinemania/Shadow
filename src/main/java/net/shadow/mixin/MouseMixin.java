package net.shadow.mixin;

import net.minecraft.client.Mouse;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.grief.WorldPainter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = {@At("RETURN")}, method = {"onMouseScroll(JDD)V"})
    private void onOnMouseScroll(long long_1, double double_1, double double_2,
                                 CallbackInfo ci) {
        if (ModuleRegistry.find("QuakeSpeed").isEnabled()) {
            if (Shadow.c.player.isOnGround()) {
                Shadow.c.player.jump();
            }
        }
        if (ModuleRegistry.find("WorldPainter").isEnabled()) {
            WorldPainter.raycast(double_2);
        }
    }
}