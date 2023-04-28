package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class MovementModule extends Module {

    final BooleanValue asprint = this.config.create("Sprint", false);
    final BooleanValue sneak = this.config.create("Sneak", false);
    final BooleanValue walk = this.config.create("Walk", false);
    final BooleanValue jump = this.config.create("Jump", false);

    public MovementModule() {
        super("Movement", "shit about da movez ", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (asprint.getThis()) {
            if (Shadow.c.player.forwardSpeed > 0) Shadow.c.player.setSprinting(true);
        }
        if (sneak.getThis()) {
            Shadow.c.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Shadow.c.player, Mode.PRESS_SHIFT_KEY));
        }
        if (walk.getThis()) {
            Shadow.c.options.forwardKey.setPressed(true);
        }
        if (jump.getThis()) {
            Shadow.c.options.jumpKey.setPressed(true);
        }
    }

    @Override
    public void onRender() {

    }
}
