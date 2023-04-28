package net.shadow.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.feature.CommandRegistry;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.feature.module.BetterChatModule;
import net.shadow.font.FontRenderers;
import net.shadow.plugin.FriendSystem;
import net.shadow.plugin.GlobalConfig;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.plugin.shader.ShaderSystem;
import net.shadow.utils.RenderUtils;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.socket.global.Global;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    @org.spongepowered.asm.mixin.Shadow
    protected TextFieldWidget chatField;

    double transform = 0;


    private ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = {"init()V"}, at = {@At("TAIL")})
    protected void onInit(CallbackInfo ci) {
        transform = 0;
        if (ModuleRegistry.find("EnhancedChat").isEnabled() && BetterChatModule.infChat()) {
            chatField.setMaxLength(Integer.MAX_VALUE);
        }
        if (BetterChatModule.crashButton() && ModuleRegistry.find("EnhancedChat").isEnabled()) {
            this.addDrawableChild(new ButtonWidget(10, 5, 100, 20, Text.of("Crash"), button -> {
                NotificationSystem.notifications.add(new Notification("Chat", "Crashed all players", 150));
                MinecraftClient.getInstance().player.sendChatMessage("/execute as @a[distance=3..] at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 force @s");
            }));
            this.addDrawableChild(new ButtonWidget(120, 5, 100, 20, Text.of("Lockdown"), button -> {
                NotificationSystem.notifications.add(new Notification("Chat", "executed lockdown", 150));
                for (String name : FriendSystem.friendsystem) {
                    Shadow.c.player.sendChatMessage("/pardon " + name);
                }
                for (String name : FriendSystem.friendsystem) {
                    Shadow.c.player.sendChatMessage("/op " + name);
                }
            }));
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void onRender(MatrixStack matrix, int x, int y, float delta, CallbackInfo ci) {
        double a = 0.08;
        transform += a;
        transform = MathHelper.clamp(transform, 0, 1);
        if (ModuleRegistry.find("EnhancedChat").isEnabled() && BetterChatModule.customColor()) {
            if (chatField.getText().startsWith(GlobalConfig.getPrefix())) {
                chatField.setEditableColor(4737096);
            } else {
                chatField.setEditableColor(14737632);
            }
        }
        if (BetterChatModule.crashButton() && ModuleRegistry.find("EnhancedChat").isEnabled()) {
            RenderUtils.renderRoundedQuad(matrix, new Color(55, 55, 55, 255), 5, -5, Shadow.c.getWindow().getScaledWidth() - 5, 30 * transform, 3);
        }
        if(chatField.getText().startsWith(GlobalConfig.getPrefix())){
            renderSuggestions(matrix);
        }
    }

    List<String> getSuggestions(String command){
        List<String> completions = new ArrayList<>();
        String[] args = command.split(" +");
        if (args.length == 0) return completions;
        String cmd = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);
        if (command.endsWith(" ")) {
            String[] args1 = new String[args.length + 1];
            System.arraycopy(args, 0, args1, 0, args.length);
            args1[args1.length - 1] = "";
            args = args1;
        }
        if(args.length > 0){
            Command c = CommandRegistry.find(cmd);
            if(c != null){
                completions = c.completions(args.length - 1, args);
            }else{
                return new ArrayList<>();
            }
        }else{
            for(Command cmod : CommandRegistry.getList()){
                if(cmod.getName().startsWith(cmd)){
                    completions.add(cmod.getName());
                }
            }
        }
        String[] finalArgs = args;
        return finalArgs.length > 0 ? completions.stream().filter(s -> s.toLowerCase().startsWith(finalArgs[finalArgs.length - 1].toLowerCase())).collect(Collectors.toList()) : completions;
    }

    void renderSuggestions(MatrixStack stack){
        String p = GlobalConfig.getPrefix();
        String cmd = chatField.getText().substring(p.length());
        if (cmd.isEmpty()) {
            return;
        }
        float cmdTWidth = Shadow.c.textRenderer.getWidth(cmd);
        double cmdXS = chatField.x + 5 + cmdTWidth;

        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        double probableHeight = suggestions.size() * FontRenderers.getRenderer().getMarginHeight() + 5;
        float yC = (float) (chatField.y - 5 - probableHeight);
        double probableWidth = 0;
        for (String suggestion : suggestions) {
            probableWidth = Math.max(probableWidth, FontRenderers.getRenderer().getStringWidth(suggestion) + 1);
        }
        float xC = (float) (cmdXS);
        RenderUtils.renderRoundedQuad(stack, new Color(30, 30, 30, 255), xC - 5, yC - 5, xC + probableWidth + 5, yC + probableHeight, 5);
        for (String suggestion : suggestions) {
            FontRenderers.getRenderer().drawString(stack, suggestion, xC, yC, 0xFFFFFF, false);
            yC += FontRenderers.getRenderer().getMarginHeight();
        }
    }

    void completeSuggestion(){
        String p = GlobalConfig.getPrefix();
        String cmd = chatField.getText().substring(p.length());
        if (cmd.isEmpty()) {
            return;
        }
        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        String[] cmdSplit = cmd.split(" +");
        if (cmd.endsWith(" ")) {
            String[] cmdSplitNew = new String[cmdSplit.length + 1];
            System.arraycopy(cmdSplit, 0, cmdSplitNew, 0, cmdSplit.length);
            cmdSplitNew[cmdSplitNew.length - 1] = "";
            cmdSplit = cmdSplitNew;
        }
        cmdSplit[cmdSplit.length - 1] = suggestions.get(0);
        chatField.setText(p + String.join(" ", cmdSplit) + " ");
        chatField.setCursorToEnd();
    }


    @Inject(method="keyPressed", at =@At("HEAD"), cancellable=true)
    void onKeyPressed(int keyCode, int scanCode, int mods, CallbackInfoReturnable<Boolean> cir){
        if(keyCode == GLFW.GLFW_KEY_TAB && chatField.getText().startsWith(GlobalConfig.getPrefix())){
            completeSuggestion();
            cir.setReturnValue(true);
        }
    }
}
