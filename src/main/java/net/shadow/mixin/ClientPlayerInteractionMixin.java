package net.shadow.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.shadow.feature.ModuleRegistry;
import net.shadow.inter.IClientPlayerInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionMixin implements IClientPlayerInteraction {
    @Shadow
    private int blockBreakingCooldown;


    @Inject(method = {"getReachDistance()F"}, at = {@At("HEAD")}, cancellable = true)
    private void onReachDistance(CallbackInfoReturnable<Float> cir) {
        if (ModuleRegistry.find("Reach").getSpecial().equalsIgnoreCase("true")) {
            cir.setReturnValue(10F);
        }
        if (ModuleRegistry.find("ReachPlus").isEnabled()) {
            cir.setReturnValue(20F);
        }
    }

    @Inject(method = {"hasExtendedReach()Z"}, at = {@At("HEAD")}, cancellable = true)
    private void onExtendedReach(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleRegistry.find("Reach").getSpecial().equalsIgnoreCase("true")) {
            cir.setReturnValue(true);
        }
        if (ModuleRegistry.find("ReachPlus").isEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public void setBlockHitDelay(int delay) {
        blockBreakingCooldown = delay;
    }

}
