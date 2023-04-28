package net.shadow.mixin;

import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @ModifyConstant(
            method = {"readNbt()Lnet/minecraft/nbt/NbtCompound;"},
            constant = {@Constant(longValue = 2097152L)}
    )
    private long onReadNbt(long nbtlength) {
        return 2000000000L;
    }
}
