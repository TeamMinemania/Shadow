package net.shadow.feature.module.movement;


import java.awt.Color;
import java.util.Random;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.NormalCube;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.event.events.RenderListener;
import net.shadow.event.events.SolidCube;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.inter.IClientPlayerEntity;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.FakePlayer;
import net.shadow.utils.RenderUtils;

public class GhostTeleportModule extends Module implements PacketOutput, PlayerMove, SolidCube, NormalCube, PacketInput, RenderListener, LeftClick {

    BlockPos targeted = new BlockPos(0,0,0);
    private FakePlayer fake;
    Vec3d exit;

    public GhostTeleportModule() {
        super("FreeTP", "Long range teleport", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        fake = new FakePlayer(Shadow.c.player);
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(SolidCube.class, this);
        Shadow.getEventSystem().add(NormalCube.class, this);
        Shadow.getEventSystem().add(PlayerMove.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(LeftClick.class, this);
        exit = Shadow.c.player.getPos();
    }

    @Override
    public void onDisable() {
        fake.despawn();
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(SolidCube.class, this);
        Shadow.getEventSystem().remove(NormalCube.class, this);
        Shadow.getEventSystem().remove(PlayerMove.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.c.player.updatePosition(exit.getX(), exit.getY(), exit.getZ());
    }

    @Override
    public void onUpdate() {
        Shadow.c.player.getAbilities().flying = false;
        double bitx = 0;
        double bity = 0;
        double bitz = 0;

        if (Shadow.c.options.backKey.isPressed()) {
            bitz += 1;
        }
        if (Shadow.c.options.forwardKey.isPressed()) {
            bitz -= 1;
        }
        if (Shadow.c.options.leftKey.isPressed()) {
            bitx -= 1;
        }
        if (Shadow.c.options.rightKey.isPressed()) {
            bitx += 1;
        }
        if (Shadow.c.options.jumpKey.isPressed()) {
            bity += 1;
        }
        if (Shadow.c.options.sneakKey.isPressed()) {
            bity -= 1;
        }
        double sinrads = Math.sin(Math.toRadians(Shadow.c.player.getYaw()));
        double cosrads = Math.cos(Math.toRadians(Shadow.c.player.getYaw()));
        double deltax = (bitz * sinrads) + (bitx * -cosrads);
        double deltaz = (bitz * -cosrads) + (bitx * -sinrads);
        Shadow.c.player.setVelocity(new Vec3d(deltax, bity, deltaz));

        
        BlockHitResult ray = (BlockHitResult) Shadow.c.player.raycast(500, Shadow.c.getTickDelta(), true);
        targeted = new BlockPos(ray.getBlockPos() != null ? ray.getBlockPos() : new BlockPos(0,0,0));
    }

    @Override
    public void onRender() {

    }

   

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            if(Shadow.tpService.getShouldBlockPackets()){
                event.cancel();
            }
        }
    }

    @Override
    public void onIsNormalCube(NormalCubeEvent event) {
        event.cancel();
    }

    @Override
    public void onSolidCube(SetSolidCube event) {
        event.cancel();
    }

    @Override
    public void onPlayerMove(IClientPlayerEntity player) {
        player.setNoClip(true);
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        Vec3d vp = new Vec3d(targeted.getX(), targeted.getY(), targeted.getZ());
        RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(100, 100, 100, 255), matrix);
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if(event.getPacket() instanceof PlayerPositionLookS2CPacket packet){
            NotificationSystem.post("GhostTeleport", "got lagback, disabling module");
            this.setEnabled(false);
        }
    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(200, Shadow.c.getTickDelta(), true);
        Shadow.tpService.gooberTP(exit, blockHitResult.getPos(), () -> {
            fake.updatePosition(blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z);
            exit = blockHitResult.getPos();
        });
    }
}
