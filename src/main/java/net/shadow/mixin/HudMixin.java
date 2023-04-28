package net.shadow.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.module.other.Unload;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.MSAAFramebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            if (!Unload.loaded) return;
            for (Module module : ModuleRegistry.getAll()) {
                if (module.isEnabled()) module.onRender();
            }
            NotificationSystem.render();
        });
    }
}
