package net.shadow.mixin;

import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.plugin.GlobalConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectScreenMixin extends Screen {

    private DisconnectScreenMixin(Text name) {
        super(name);
    }

    @Inject(at = {@At("TAIL")}, method = {"init()V"})
    private void onInit(CallbackInfo ci) {
        addDrawableChild(new ButtonWidget(0, 0, 100, 20, Text.of("Change Account"), b -> quickChange()));
        if (GlobalConfig.reconnectInstantly) {
            ConnectScreen.connect(this, Shadow.c, GlobalConfig.serverAddress, new ServerInfo("cock", GlobalConfig.serverAddress.getAddress(), false));
            GlobalConfig.reconnectInstantly = false;
        }
    }


    private void quickChange() {

    }
}
