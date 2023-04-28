package net.shadow.mixin;

import com.google.gson.JsonArray;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.LeftClick.LeftClickEvent;
import net.shadow.event.events.MiddleClick.MiddleClickEvent;
import net.shadow.event.events.RightClick.RightClickEvent;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Module;
import net.shadow.feature.module.other.Unload;
import net.shadow.font.FontRenderers;
import net.shadow.font.adapter.impl.BaseAdapter;
import net.shadow.font.render.FontRenderer;
import net.shadow.inter.IClientPlayerEntity;
import net.shadow.inter.IClientPlayerInteraction;
import net.shadow.inter.MClientI;
import net.shadow.plugin.BetterItems;
import net.shadow.plugin.GameConfig;
import net.shadow.plugin.Keybinds;
import net.shadow.scripting.Executor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;


@Mixin(value = MinecraftClient.class)
public class MinecraftClientMixin implements MClientI {

    @Shadow
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "stop", at = @At("HEAD"))
    public void onUnloadGame(CallbackInfo ci) {
        if (!Unload.loaded) return;
        GameConfig.save();
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;printCrashReport(Lnet/minecraft/util/crash/CrashReport;)V"))
    public void onSaveCrashReport(CallbackInfo info) {
        if (!Unload.loaded) return;
        GameConfig.save();
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void onGameUpdate(CallbackInfo ci) {
        if (!GameConfig.isLoaded()) GameConfig.load();
        if (MinecraftClient.getInstance().player != null) {
            for (Module m : ModuleRegistry.getAll()) {
                if (!Unload.loaded && m.isEnabled()) m.setEnabled(false);
                if (m.isEnabled()) m.onUpdate();
            }
            for (JsonArray json : Executor.tickFunctionTable.values()) {
                Executor.exec(json);
            }
            BetterItems.onUpdate();
        }
        Keybinds.call();
        ModuleRegistry.find("Prefix").onUpdate();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void postInit(RunArgs args, CallbackInfo ci) {
        try {
            int fsize = 18 * 2;
            FontRenderers.setRenderer(new BaseAdapter(new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(net.shadow.Shadow.class.getClassLoader().getResourceAsStream("Mono.ttf"))).deriveFont(Font.PLAIN, fsize), fsize)));
            net.shadow.Shadow.startAutoSave();
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"doAttack"}, cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        LeftClickEvent event = new LeftClickEvent();
        EventHandler.call(event);
        for (JsonArray json : Executor.leftClickTable.values()) {
            Executor.exec(json);
        }
        if (event.isCancelled())
            cir.setReturnValue(false);
    }

    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", ordinal = 0)}, method = {"doItemUse()V"}, cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        RightClickEvent event = new RightClickEvent();
        EventHandler.call(event);
        for (JsonArray json : Executor.rightClickTable.values()) {
            Executor.exec(json);
        }
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"doItemPick()V"}, cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        MiddleClickEvent event = new MiddleClickEvent();
        EventHandler.call(event);
        for (JsonArray json : Executor.middleClickTable.values()) {
            Executor.exec(json);
        }
        if (event.isCancelled())
            ci.cancel();
    }

    @Override
    public int getItemUseCooldown() {
        return itemUseCooldown;
    }

    @Override
    public void setItemUseCooldown(int itemUseCooldown) {
        this.itemUseCooldown = itemUseCooldown;
    }

    @Override
    public IClientPlayerEntity getPlayer() {
        return (IClientPlayerEntity) player;
    }

    @Override
    public IClientPlayerInteraction getInteractions() {
        return (IClientPlayerInteraction) interactionManager;
    }
}
