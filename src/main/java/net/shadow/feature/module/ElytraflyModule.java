package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PlayerMove;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.inter.IClientPlayerEntity;

public class ElytraflyModule extends Module implements PlayerMove {
    final SliderValue speed = this.config.create("Speed", 2, 0.25, 5, 1);
    final BooleanValue autofly = this.config.create("AutoFly", false);

    public ElytraflyModule() {
        super("Elytrafly", "fly forever with elytras", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PlayerMove.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PlayerMove.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onPlayerMove(IClientPlayerEntity player) {
        if (Shadow.c.player.isFallFlying()) {
            Shadow.c.player.getAbilities().flying = false;
            double bitx = 0;
            double bity = 0;
            double bitz = 0;

            if (Shadow.c.options.backKey.isPressed()) {
                bitz += speed.getThis();
            }
            if (Shadow.c.options.forwardKey.isPressed()) {
                bitz -= speed.getThis();
            }
            if (Shadow.c.options.leftKey.isPressed()) {
                bitx -= speed.getThis();
            }
            if (Shadow.c.options.rightKey.isPressed()) {
                bitx += speed.getThis();
            }
            if (Shadow.c.options.jumpKey.isPressed()) {
                bity += speed.getThis();
            }
            if (Shadow.c.options.sneakKey.isPressed()) {
                bity -= speed.getThis();
            }
            double sinrads = Math.sin(Math.toRadians(Shadow.c.player.getYaw()));
            double cosrads = Math.cos(Math.toRadians(Shadow.c.player.getYaw()));
            double deltax = (bitz * sinrads) + (bitx * -cosrads);
            double deltaz = (bitz * -cosrads) + (bitx * -sinrads);
            Shadow.c.player.setVelocity(new Vec3d(deltax, bity, deltaz));
        } else {
            if (autofly.getThis()) {
                Shadow.c.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Shadow.c.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }
}
