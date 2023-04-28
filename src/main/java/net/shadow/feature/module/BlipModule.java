package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.NormalCube;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.event.events.SolidCube;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.inter.IClientPlayerEntity;

public class BlipModule extends Module implements PlayerMove, NormalCube, SolidCube, PacketOutput {

    final SliderValue s = this.config.create("Speed", 1, 1, 10, 1);

    public BlipModule() {
        super("Blip", "teleport around out of your body", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(SolidCube.class, this);
        Shadow.getEventSystem().add(NormalCube.class, this);
        Shadow.getEventSystem().add(PlayerMove.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(SolidCube.class, this);
        Shadow.getEventSystem().remove(NormalCube.class, this);
        Shadow.getEventSystem().remove(PlayerMove.class, this);

        BlockPos pos = new BlockPos(Shadow.c.player.getPos());
        Shadow.c.player.setVelocity(0, 0, 0);
        Shadow.c.player.updatePosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Shadow.c.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = Shadow.c.player;
        player.setVelocity(Vec3d.ZERO);
        player.noClip = true;
        player.fallDistance = 0;
        player.getAbilities().flying = false;
        player.setOnGround(false);
        player.airStrafingSpeed = (float) Math.round(s.getThis());
        Vec3d velcity = player.getVelocity();

        if (Shadow.c.options.jumpKey.isPressed())
            player.setVelocity(velcity.add(0, s.getThis(), 0));

        if (Shadow.c.options.sneakKey.isPressed())
            player.setVelocity(velcity.subtract(0, s.getThis(), 0));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSolidCube(SetSolidCube event) {
        event.cancel();
    }

    @Override
    public void onIsNormalCube(NormalCubeEvent event) {
        event.cancel();
    }

    @Override
    public void onPlayerMove(IClientPlayerEntity player) {
        player.setNoClip(true);
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket)
            event.cancel();
    }


}
