package net.shadow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMovePacketMixin {
    
    @Mutable
    @Accessor("onGround")
    void setOnGround(boolean onGround);

    @Mutable
    @Accessor("x")
    void setX(double x);

    @Mutable
    @Accessor("y")
    void setY(double x);

    @Mutable
    @Accessor("z")
    void setZ(double x);

    @Mutable
    @Accessor("yaw")
    void setYaw(float yaw);

    @Mutable
    @Accessor("pitch")
    void setPitch(float pitch);
}
