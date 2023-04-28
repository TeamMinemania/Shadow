package net.shadow.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.shadow.gui.ServerInfoGUI;
import net.shadow.plugin.GlobalConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    private static void connect(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        GlobalConfig.serverAddress = address;
        GlobalConfig.sinfo = info;
        ServerInfoGUI.updateServerInfo(address.getAddress(), info.name, info.isLocal(), info.getIcon(), info.online, info.ping, info.playerCountLabel, info.protocolVersion);
    }
}
