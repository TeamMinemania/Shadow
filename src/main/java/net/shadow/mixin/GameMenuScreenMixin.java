package net.shadow.mixin;

import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.module.other.Unload;
import net.shadow.gui.ShadowScreenIMGUI;
import net.shadow.plugin.GlobalConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class) // good thing 0x150 had to search for 20 minutes for this and not me
public class GameMenuScreenMixin extends Screen {
    public GameMenuScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        if (!Unload.loaded) return;
        ButtonWidget bw = new ButtonWidget(2, 2, 100, 20, Text.of("Server"), button -> Shadow.c.setScreen(new DirectConnectScreen(this, null, GlobalConfig.sinfo)));
        ButtonWidget bs = new ButtonWidget(2, Shadow.c.getWindow().getScaledHeight() - 22, 100, 20, Text.of("Shadow"), button -> Shadow.c.setScreen(new ShadowScreenIMGUI()));
        this.addDrawableChild(bw);
        this.addDrawableChild(bs);
    }
}