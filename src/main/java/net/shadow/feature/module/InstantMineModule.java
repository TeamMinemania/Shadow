package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class InstantMineModule extends Module implements LeftClick, RenderListener, PacketOutput {
    static final ArrayList<BlockPos> hunter = new ArrayList<>();
    static final ArrayList<BlockPos> qremove = new ArrayList<>();
    static BlockPos target = null;
    final MultiValue mode = this.config.create("Mode", "OneBlock", "OneBlock", "Faster", "MultiSelect");

    public InstantMineModule() {
        super("InstantBreak", "mine bloc fast", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        target = null;
        hunter.clear();
    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("OneBlock")) {
            if (target != null) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, target, Direction.UP));
            }
        } else if (mode.getThis().equalsIgnoreCase("Faster")) {
            if (Shadow.c.interactionManager.isBreakingBlock()) {
                try {
                    BlockPos killmeplz = ((BlockHitResult) Shadow.c.crosshairTarget).getBlockPos();
                    for (int i = 0; i < 20; i++) {
                        Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, killmeplz, Direction.UP));
                    }
                } catch (Exception ignored) {
                }
            }
        } else {
            for (BlockPos pos : hunter) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
                if (Shadow.c.world.getBlockState(pos).isAir()) {
                    qremove.add(pos);
                }
            }
            for (BlockPos pos : qremove) {
                hunter.remove(pos);
            }
            qremove.clear();
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        BlockHitResult bhr = (BlockHitResult) Shadow.c.crosshairTarget;
        target = bhr.getBlockPos();
        if (mode.getThis().equalsIgnoreCase("MultiSelect")) {
            hunter.add(bhr.getBlockPos());
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (mode.getThis().equalsIgnoreCase("OneBlock")) {
            if (target != null) {
                Vec3d targ = new Vec3d(target.getX(), target.getY(), target.getZ());
                RenderUtils.renderObject(targ, new Vec3d(1, 1, 1), new Color(50, 50, 50, 100), matrix);
            }
        } else if (mode.getThis().equalsIgnoreCase("MultiSelect")) {
            for (BlockPos target2 : hunter) {
                Vec3d targ = new Vec3d(target2.getX(), target2.getY(), target2.getZ());
                RenderUtils.renderObject(targ, new Vec3d(1, 1, 1), new Color(50, 50, 50, 100), matrix);
            }
        }
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (mode.getThis().equalsIgnoreCase("multi")) {
            if (event.getPacket() instanceof PlayerActionC2SPacket packet) {
                if (packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
                    event.cancel();
                }
            }
        }
    }
}
