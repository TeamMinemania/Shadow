package net.shadow.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.shadow.feature.ModuleRegistry;
import net.shadow.plugin.quake.QuakeClientPlayer;
import net.shadow.plugin.quake.QuakeServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.find("QuakeSpeed").isEnabled())
            return;

        if (QuakeClientPlayer.travel((PlayerEntity) (Object) this, movementInput))
            info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void beforeUpdate(CallbackInfo info) {
        QuakeClientPlayer.beforeOnLivingUpdate((PlayerEntity) (Object) this);
    }

    @Inject(at = @At("TAIL"), method = "jump()V")
    private void afterJump(CallbackInfo info) {
        QuakeClientPlayer.afterJump((PlayerEntity) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z")
    private void beforeFall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        QuakeServerPlayer.beforeFall((PlayerEntity) (Object) this, fallDistance, damageMultiplier);
    }

    @Inject(at = @At("TAIL"), method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z")
    private void afterFall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        QuakeServerPlayer.afterFall((PlayerEntity) (Object) this, fallDistance, damageMultiplier);
    }
}