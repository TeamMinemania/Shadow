package net.shadow.inter;

import net.minecraft.util.math.Vec3d;

public interface IClientPlayerEntity {
    void setNoClip(boolean noClip);

    void setMovementMultiplier(Vec3d movementMultiplier);
}
