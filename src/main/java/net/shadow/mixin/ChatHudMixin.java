//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.ChatInput.ChatInputEvent;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.BlurModule;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.plugin.shader.ShaderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin extends DrawableHelper {
    @Final
    @Shadow
    private List<ChatHudLine<OrderedText>> visibleMessages;
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
    private void onAddMessage(Text chatText, int chatLineId, CallbackInfo ci) {

    }

    @Inject(at = @At("HEAD"), method="render", cancellable = true)
    public void render(net.minecraft.client.util.math.MatrixStack matrices, int tickDelta, CallbackInfo ci){
        if(net.shadow.Shadow.c.currentScreen instanceof ChatScreen && ModuleRegistry.getByClass(BlurModule.class).isEnabled()){
            ShaderSystem.BLUR.getEffect().setUniformValue("progress", 1F);
            ShaderSystem.BLUR.render(tickDelta);
        }
    }
} 