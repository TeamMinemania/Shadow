package net.shadow.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.AntiCrashModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleMixin {
    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    void disableParticleAdd(Particle particle, CallbackInfo ci) {
        if (ModuleRegistry.find("AntiCrash").isEnabled() && AntiCrashModule.shouldCapAoes()) ci.cancel();
    }
}
