package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.other.ScreenSaver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class ScreenMixin extends Screen {

    protected ScreenMixin() {
        super(Text.of("abc"));
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void onInit(CallbackInfo ci) {
        if (ModuleRegistry.find("ScreenSaver").isEnabled()) {
            addDrawableChild(new ButtonWidget(3, 3, 100, 20, Text.of("Save"), b -> {
                ScreenSaver.setSpoof((HandledScreen<?>) (Object) this);
                Shadow.c.setScreen(null);
                Shadow.c.player.currentScreenHandler = Shadow.c.player.playerScreenHandler;
            }));
        }
    }
}
