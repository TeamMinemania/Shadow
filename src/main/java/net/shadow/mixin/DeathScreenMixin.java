package net.shadow.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.Death.DeathEvent;
import net.shadow.feature.module.other.Unload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = {@At(value = "TAIL")}, method = {"tick()V"})
    private void onTick(CallbackInfo ci) {
        if (!Unload.loaded) return;
        DeathEvent event = new DeathEvent();
        EventHandler.call(event);
    }
}