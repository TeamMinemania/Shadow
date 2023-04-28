package net.shadow.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.KeyboardListener.KeyboardEvent;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.other.ScreenSaver;
import net.shadow.feature.module.other.Unload;
import net.shadow.gui.ConsoleScreen;
import net.shadow.gui.SpotlightScreen;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Keyboard.class)
public class KeyboardMixin {
    @org.spongepowered.asm.mixin.Shadow
    private boolean repeatEvents;

    @org.spongepowered.asm.mixin.Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "setRepeatEvents", at = @At("HEAD"), cancellable = true)
    public void setRepeatEvents(boolean repeatEvents, CallbackInfo ci) {
        this.repeatEvents = true;
        ci.cancel();
    }

    @Inject(method = "onKey", at = @At("RETURN"))
    void keyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (!Unload.loaded) return;
        if (window == this.client.getWindow().getHandle() && Shadow.c.currentScreen == null) {
            if (Shadow.c.player == null || Shadow.c.world == null) return;
            KeyboardEvent event = new KeyboardEvent(key, action);
            if (key == GLFW.GLFW_KEY_BACKSLASH && action == 1) {
                if (ModuleRegistry.find("ScreenSaver").isEnabled()) {
                    ScreenSaver.pingSpoof();
                }
            }
            EventHandler.call(event);
        }
        if (window == this.client.getWindow().getHandle() && Shadow.c.currentScreen == null) { // make sure we are in game and the screen has been there for at least 10 ms
            if (Shadow.c.player == null || Shadow.c.world == null) {
                return; // again, make sure we are in game and exist
            }
            if(key == 46 && modifiers == 1){
                Shadow.c.setScreen(ConsoleScreen.instance());
            }
            if(key == 46 && modifiers == 0){
                Shadow.c.setScreen(new SpotlightScreen(Text.of("")));
            }
        }
    }
}