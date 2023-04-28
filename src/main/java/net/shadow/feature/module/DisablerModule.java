package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class DisablerModule extends Module {
    static int renderticks;
    static int updateticks;
    final MultiValue mode = this.config.create("Mode", "Vanilla", "Vanilla", "SuperVanilla", "OldMatrix");

    public DisablerModule() {
        super("Disabler", "make hacks work better", ModuleType.OTHER);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "[" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        updateticks++;
        if (mode.getThis().equalsIgnoreCase("oldmatrix")) {
            if (updateticks % 5 == 0) {
                Shadow.c.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Shadow.c.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }

    @Override
    public void onRender() {
        renderticks++;
        if (mode.getThis().equalsIgnoreCase("supervanilla")) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX(), Shadow.c.player.getY(), Shadow.c.player.getZ(), Shadow.c.player.isOnGround()));
        }
        if (mode.getThis().equalsIgnoreCase("vanilla")) {
            if (renderticks % 2 == 0) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX(), Shadow.c.player.getY(), Shadow.c.player.getZ(), Shadow.c.player.isOnGround()));
            }
        }
    }
}
