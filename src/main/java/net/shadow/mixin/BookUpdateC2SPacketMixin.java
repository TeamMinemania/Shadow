package net.shadow.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.shadow.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(BookUpdateC2SPacket.class)
public abstract class BookUpdateC2SPacketMixin {

    @Inject(method = "write", cancellable = true, at = @At("HEAD"))
    public void gigi(PacketByteBuf buf, CallbackInfo ci) {
    }
}
