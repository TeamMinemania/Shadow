/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.movement.Jesus;
import net.shadow.client.feature.module.impl.movement.NoLevitation;
import net.shadow.client.feature.module.impl.movement.NoPush;
import net.shadow.client.feature.module.impl.render.FreeLook;
import net.shadow.client.helper.manager.AttackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void atomic_setLastAttacked(Entity target, CallbackInfo ci) {
        if (this.equals(ShadowMain.client.player) && target instanceof LivingEntity entity) {
            AttackManager.registerLastAttacked(entity);
        }
    }

    //    @Redirect(method="travel",at=@At(value="INVOKE",target="Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    //    void mulVel(LivingEntity instance, double x, double y, double z)
    //        instance.setVelocity(x,y,z);
    //    }
    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    public void atomic_overwriteCanWalkOnFluid(FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (ShadowMain.client.player == null) {
            return;
        }
        // shut up monkey these are mixins you fucking idiot
        if (this.equals(ShadowMain.client.player)) {
            Jesus jesus = ModuleRegistry.getByClass(Jesus.class);
            if (jesus.isEnabled() && jesus.mode.getValue() == Jesus.Mode.Solid) {
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"), require = 0)
    boolean atomic_stopLevitationEffect(LivingEntity instance, StatusEffect effect) {
        if (instance.equals(ShadowMain.client.player) && ModuleRegistry.getByClass(NoLevitation.class).isEnabled() && effect == StatusEffects.LEVITATION) {
            return false;
        } else {
            return instance.hasStatusEffect(effect);
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void atomic_cancelPush(Entity entity, CallbackInfo ci) {
        if (this.equals(ShadowMain.client.player)) {
            if (Objects.requireNonNull(ModuleRegistry.getByClass(NoPush.class)).isEnabled()) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.getYaw()F"), require = 0)
    private float atomic_overwriteFreelookYaw(LivingEntity instance) {
        if (instance.equals(ShadowMain.client.player) && ModuleRegistry.getByClass(FreeLook.class).isEnabled()) {
            return ModuleRegistry.getByClass(FreeLook.class).newyaw;
        }
        return instance.getYaw();
    }
}
