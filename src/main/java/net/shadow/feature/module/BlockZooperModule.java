package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockZooperModule extends Module implements RenderListener {
    final List<BlockPos> renders = new ArrayList<>();

    public BlockZooperModule() {
        super("BlockReload", "block zoinker", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        BlockPos ppos = Shadow.c.player.getBlockPos();
        new Thread(() -> {
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = ppos.add(new BlockPos(x * 3, y * 3, z * 3));
                        if (Shadow.c.world.getBlockState(pos).isAir()) continue;
                        renders.add(pos);
                        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, false)));
                        Utils.sleep(5);
                    }
            this.setEnabled(false);
        }).start();
    }

    @Override
    public void onDisable() {
        renders.clear();
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (BlockPos render : new ArrayList<>(renders)) {
            Vec3d vp = new Vec3d(render.getX(), render.getY(), render.getZ());
            RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        }
    }
}
