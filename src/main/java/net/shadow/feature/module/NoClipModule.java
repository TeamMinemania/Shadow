package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.NormalCube;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.event.events.SolidCube;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.inter.IClientPlayerEntity;

public class NoClipModule extends Module implements PlayerMove, NormalCube, SolidCube, PacketOutput {

    final MultiValue m = this.config.create("Mode", "Suspend", "Suspend", "Normal", "2D", "Packet");
    final SliderValue speed = this.config.create("Speed", 2, 0.1, 5, 1);

    private int teleportId;
    private int ticksExisted;

    public NoClipModule() {
        super("NoClip", "noclip through blocks", ModuleType.MOVEMENT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PlayerMove.class, this);
        Shadow.getEventSystem().add(NormalCube.class, this);
        Shadow.getEventSystem().add(SolidCube.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PlayerMove.class, this);
        Shadow.getEventSystem().remove(NormalCube.class, this);
        Shadow.getEventSystem().remove(SolidCube.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.c.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = Shadow.c.player;
        Vec3d velcity = Shadow.c.player.getVelocity();

        switch (m.getThis().toLowerCase()) {
            case "2d" -> {
                player.noClip = true;
                player.fallDistance = 0;
                player.setOnGround(false);
                player.setPose(EntityPose.STANDING);
                player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "");
                player.setVelocity(0, 0, 0);
                player.getAbilities().flying = false;
                if (Shadow.c.options.jumpKey.isPressed())
                    player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "") * 2;
                if (Shadow.c.options.sneakKey.isPressed())
                    player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "") / 2;
            }
            case "normal", "suspend" -> {
                player.noClip = true;
                player.fallDistance = 0;
                player.setOnGround(false);
                player.setPose(EntityPose.STANDING);
                player.airStrafingSpeed = Float.parseFloat(speed.getThis() + "");
                player.setVelocity(0, 0, 0);
                player.getAbilities().flying = false;
                if (Shadow.c.options.jumpKey.isPressed())
                    player.addVelocity(0, speed.getThis(), 0);
                if (Shadow.c.options.sneakKey.isPressed())
                    player.addVelocity(0, speed.getThis() * -1, 0);
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        Box box = Shadow.c.player.getBoundingBox().offset(0, 0.27, 0).expand(0.25);
        if (box.getYLength() < 2) {
            box = box.expand(0, 1, 0);
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket && m.getThis().equalsIgnoreCase("suspend") && !Shadow.c.world.isSpaceEmpty(Shadow.c.player, box))
            event.cancel();

        if (event.getPacket() instanceof PlayerMoveC2SPacket && m.getThis().equalsIgnoreCase("onflying") && !Shadow.c.world.isSpaceEmpty(Shadow.c.player, box) && Shadow.c.player.getAbilities().flying) {
            event.cancel();
        }
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
}
