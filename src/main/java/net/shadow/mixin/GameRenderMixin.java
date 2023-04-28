package net.shadow.mixin;

import com.google.gson.JsonArray;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.RenderListener.RenderEvent;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.other.Unload;
import net.shadow.plugin.BetterItems;
import net.shadow.scripting.Executor;
import net.shadow.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0)}, method = {"renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"})
    private void onRenderWorld(float partialTicks, long donetype, MatrixStack matrix, CallbackInfo ci) {
        BetterItems.onRender(partialTicks, matrix);
        if (!Unload.loaded) return;
        for (JsonArray json : Executor.renderTable.values()) {
            Executor.exec(json);
        }
        Utils.render();
        RenderEvent event = new RenderEvent(partialTicks, matrix);
        EventHandler.call(event);
    }

    @Inject(at = {@At("HEAD")}, method = {"bobViewWhenHurt"}, cancellable = true)
    private void onBobViewWhenHurt(MatrixStack matrixStack, float f,
                                   CallbackInfo ci) {
        if (Utils.parseFromCompoundString(ModuleRegistry.find("ViewChanges").getSpecial(), 0) && ModuleRegistry.find("ViewChanges").isEnabled()) {
            ci.cancel();
        }
    }

}
