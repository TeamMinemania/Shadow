package net.shadow.mixin;

import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends HandledScreen {

    public BeaconScreenMixin(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        if (ModuleRegistry.find("BeaconDelimiter").isEnabled()) {
            this.addDrawableChild(new ButtonWidget(1,
                    1, 100, 20, new LiteralText("Custom"),
                    b -> {
                        int ii = Integer.parseInt(ModuleRegistry.find("BeconDelimiter").getSpecial());
                        Shadow.c.player.networkHandler.sendPacket(new UpdateBeaconC2SPacket(ii, ii));
                    }));
        }
    }
}
