package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.module.other.Unload;
import net.shadow.plugin.GlobalConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin extends Screen {
    protected CreativeInventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo ci) {
        if (!Unload.loaded) return;
        ButtonWidget catsel = new ButtonWidget(5, Shadow.c.getWindow().getScaledHeight() - 25, 100, 20, Text.of("Custom: " + GlobalConfig.accessing), button -> {
            if (GlobalConfig.accessing.equals("Items")) {
                GlobalConfig.accessing = "Public";
                Shadow.c.setScreen(new CreativeInventoryScreen(Shadow.c.player));
                return;
            }
            if (GlobalConfig.accessing.equals("Public")) {
                GlobalConfig.accessing = "Logger";
                Shadow.c.setScreen(new CreativeInventoryScreen(Shadow.c.player));
                return;
            }
            if (GlobalConfig.accessing.equals("Logger")) {
                GlobalConfig.accessing = "Items";
                Shadow.c.setScreen(new CreativeInventoryScreen(Shadow.c.player));
            }
        });
        addDrawableChild(catsel);
    }
}
