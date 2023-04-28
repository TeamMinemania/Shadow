package net.shadow.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.EntitySpawn.EntitySpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Tigermouthbear
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("RETURN"))
    public void addEntity(int id, Entity entity, CallbackInfo ci) {
        EntitySpawnEvent event = new EntitySpawnEvent(entity);
        EventHandler.call(event);
    }
}
