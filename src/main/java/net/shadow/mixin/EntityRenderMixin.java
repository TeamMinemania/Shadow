package net.shadow.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.NametagsModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    protected abstract void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabel(T entity, Text t, MatrixStack matrix, VertexConsumerProvider vcp, int int1, CallbackInfo ci) {
    }


    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    ), require = 0)
    public void renderLabel1(EntityRenderer<?> entityRenderer, T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (ModuleRegistry.find("AntiCrash").isEnabled() && text.getString().length() > 127)
            text = Text.of("...");
        renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
    }
}
