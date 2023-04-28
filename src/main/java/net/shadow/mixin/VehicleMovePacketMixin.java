package net.shadow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

@Mixin(VehicleMoveC2SPacket.class)
public interface VehicleMovePacketMixin {

    @Mutable
    @Accessor("x")
    void setX(double x);

    @Mutable
    @Accessor("y")
    void setY(double x);

    @Mutable
    @Accessor("z")
    void setZ(double x);
}
