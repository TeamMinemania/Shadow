package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.module.other.Unload;
import net.shadow.inter.IMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen implements IMultiplayerScreen {

    @org.spongepowered.asm.mixin.Shadow
    protected MultiplayerServerListWidget serverListWidget;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = {@At("TAIL")}, method = {"init()V"})
    private void onInit(CallbackInfo ci) {
        if (!Unload.loaded) return;
        ButtonWidget bu = new ButtonWidget(2, 2, 100, 20, Text.of("Crash"), button -> {

        });

        ButtonWidget clean = new ButtonWidget(104, 2, 100, 20, Text.of("Disable"), button -> {
            MultiplayerScreen MPInstance = ((MultiplayerScreen) (Object) this);
            Shadow.c.setScreen(new TitleScreen());
            new Thread(() -> {
                while (hasMolenheimerServers()) {
                    for (int i = 0; i < MPInstance.getServerList().size(); i++) {
                        if (MPInstance.getServerList().get(i).name.contains("Molenheimer")) {
                            MPInstance.getServerList().remove(MPInstance.getServerList().get(i));
                            ((IMultiplayerScreen) MPInstance).getServerListSelector().setSelected(null);
                            ((IMultiplayerScreen) MPInstance).getServerListSelector().setServers(MPInstance.getServerList());
                        }
                    }
                    MPInstance.getServerList().saveFile();
                }
            }).start();
        });
        addDrawableChild(bu);
        addDrawableChild(clean);
    }

    @Override
    public MultiplayerServerListWidget getServerListSelector() {
        return serverListWidget;
    }

    @Override
    public void connectToServer(ServerInfo server) {

    }


    public boolean hasMolenheimerServers() {
        MultiplayerScreen MPInstance = ((MultiplayerScreen) (Object) this);
        for (int i = 0; i < MPInstance.getServerList().size(); i++) {
            if (MPInstance.getServerList().get(i).name.contains("Molenheimer")) {
                return true;
            }
        }
        return false;
    }
}
