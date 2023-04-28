package net.shadow.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.shadow.feature.ModuleRegistry;
import net.shadow.plugin.quake.QuakeClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void updateVelocity(float movementSpeed, Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.find("QuakeSpeed").isEnabled())
            return;

        if (QuakeClientPlayer.updateVelocity((Entity) (Object) this, movementInput, movementSpeed))
            info.cancel();
    }
}