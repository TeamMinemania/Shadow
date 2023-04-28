package net.shadow.plugin.quake;

import net.minecraft.entity.player.PlayerEntity;

public class QuakeServerPlayer {
    private static boolean wasVelocityChangedBeforeFall = false;

    public static void beforeFall(PlayerEntity player, float fallDistance, float damageMultiplier) {
        if (player.world.isClient)
            return;

        wasVelocityChangedBeforeFall = player.velocityModified;
    }

    public static void afterFall(PlayerEntity player, float fallDistance, float damageMultiplier) {
        if (player.world.isClient)
            return;

        player.velocityModified = wasVelocityChangedBeforeFall;
    }
}