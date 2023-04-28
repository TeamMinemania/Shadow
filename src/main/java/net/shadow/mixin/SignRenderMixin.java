package net.shadow.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.utils.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public class SignRenderMixin {

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"))
    private void render(SignBlockEntity signBlockEntity_1, float float_1, MatrixStack matrixStack_1, VertexConsumerProvider vertexConsumerProvider_1, int int_1,
                        int int_2, CallbackInfo ci) {
        if (ModuleRegistry.find("PlayerFinder").isEnabled()) {
            if (!ModuleRegistry.find("PlayerFinder").getSpecial().equalsIgnoreCase("freedom")) return;
            try {
                String pos = "\u00a7f" + signBlockEntity_1.getTextOnRow(0, true).asString();
                String dim = "\u00a7f" + signBlockEntity_1.getTextOnRow(1, true).asString();
                if (pos.length() < 7) return;
                pos = pos.replace("[", "");
                pos = pos.replace("]", "");
                pos = pos.replace("d", "");
                String[] coords = pos.split(",");
                ChatUtils.message("located at " + coords[0] + " " + coords[1] + " " + coords[2]);
                ChatUtils.message("in dimension " + dim);
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, Shadow.c.player.getBlockPos(), Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, Shadow.c.player.getBlockPos(), Direction.UP));
                ModuleRegistry.find("PlayerFinder").setEnabled(false);
            } catch (Exception e) {
                ChatUtils.message("tried to render wall sign first! try again!");
                ModuleRegistry.find("PlayerFinder").setEnabled(false);
            }

        }
    }

}