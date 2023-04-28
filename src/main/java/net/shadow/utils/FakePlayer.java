package net.shadow.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.shadow.Shadow;

public class FakePlayer extends OtherClientPlayerEntity {
    //this is all stolen from wurst, but i had to fucking change it
    private final ClientPlayerEntity player = Shadow.c.player;
    private final ClientWorld world = Shadow.c.world;

    public FakePlayer(PlayerEntity e) {
        super(Shadow.c.world, e.getGameProfile());
        copyPositionAndRotation(player);


        copyPlayerModel(e, this);
        copyRotation();
        resetCapeMovement();

        spawn();
    }

    private void copyPlayerModel(Entity from, Entity to) {
        DataTracker fromTracker = from.getDataTracker();
        DataTracker toTracker = to.getDataTracker();
        Byte playerModel = fromTracker.get(PlayerEntity.PLAYER_MODEL_PARTS);
        toTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
    }

    private void copyRotation() {
        headYaw = player.headYaw;
        bodyYaw = player.bodyYaw;
    }

    private void resetCapeMovement() {
        capeX = getX();
        capeY = getY();
        capeZ = getZ();
    }

    private void spawn() {
        world.addEntity(getId(), this);
    }

    public void despawn() {
        Shadow.c.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    public void resetPlayerPosition() {
        player.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
    }
}
