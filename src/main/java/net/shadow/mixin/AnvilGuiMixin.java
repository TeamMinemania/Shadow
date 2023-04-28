package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.shadow.feature.module.other.Unload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AnvilScreen.class)
public abstract class AnvilGuiMixin extends Screen {

    @org.spongepowered.asm.mixin.Shadow
    private TextFieldWidget nameField;

    protected AnvilGuiMixin(Text title) {
        super(title);
    }

    @Inject(method = "setup", at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        if (!Unload.loaded) return;
        nameField.setMaxLength(35565);
    }
}
