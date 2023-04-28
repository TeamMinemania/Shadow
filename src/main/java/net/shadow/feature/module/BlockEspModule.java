package net.shadow.feature.module;

import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockEspModule extends Module implements RenderListener {
    static final List<BlockPos> blocks = new ArrayList<>();
    static int ticks;
    final CustomValue<String> block = this.config.create("Block", "diamond_ore");
    final SliderValue range = this.config.create("Range", 1, 25, 500, 1);
    boolean hasshowednotif = false;

    public BlockEspModule() {
        super("BlockESP", "xray but better", ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        hasshowednotif = false;
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        ticks++;
        if (ticks % 400 == 0) {
            new Thread(() -> {
                blocks.clear();
                int size = (int) Math.round(range.getThis());
                Block wanted = Registry.BLOCK.get(new Identifier(block.getThis()));
                for (int x = size * -1; x < size; x++)
                    for (int y = size * -1; y < size; y++)
                        for (int z = size * -1; z < size; z++) {
                            BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                            if (Shadow.c.world.getBlockState(pos).getBlock().equals(wanted)) {
                                blocks.add(pos);
                            }
                        }
            }).start();
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (blocks.size() > 2000 && !hasshowednotif) {
            NotificationSystem.notifications.add(new Notification("BlockESP", "Too many blocks found!", 150));
            hasshowednotif = true;
        }
        if (blocks.size() > 2000) {
            for (int i = 0; i < blocks.size(); i++) {
                if (i < 1999) {
                    blocks.remove(i);
                }
            }
        }
        for (BlockPos ticked : blocks) {
            RenderUtils.renderObject(new Vec3d(ticked.getX(), ticked.getY(), ticked.getZ()), new Vec3d(1, 1, 1), new Color(55, 50, 94, 150), matrix);
        }
    }
}
