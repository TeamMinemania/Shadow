package net.shadow.feature.module;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.Utils;

public class DecimatorModule extends Module {

    final CustomValue<String> material = this.config.create("Material", "air");
    final BooleanValue minworld = this.config.create("LowWorldDepth", false);

    public DecimatorModule() {
        super("Decimator", "like annihilator but different", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        BlockPos playerpos = Shadow.c.player.getBlockPos();
        NotificationSystem.notifications.add(new Notification("Decimator", "Decimating...", 500));
        new Thread(() -> {
            for (int x = -5; x < 5; x++)
                for (int y = -5; y < 5; y++)
                    for (int z = -5; z < 5; z++) {
                        BlockPos pos = playerpos.add(new BlockPos(x * 15, y * 15, z * 15));
                        if (Shadow.c.world.getBlockState(pos).isAir() || Shadow.c.world.getBlockState(pos).getBlock().equals(Blocks.VOID_AIR))
                            continue;
                        fillFrom(pos.getX(), pos.getY(), pos.getZ(), 15);
                        Utils.sleep(2);
                    }
            NotificationSystem.notifications.add(new Notification("Decimator", "Done Decimating", 150));
            this.setEnabled(false);
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

    void fillFrom(int x, int y, int z, int size) {
        int x1 = x + size;
        int y1 = y + size;
        int z1 = z + size;
        int x2 = x - size;
        int y2 = y - size;
        int z2 = z - size;
        if (y2 < (minworld.getThis() ? -64 : 0)) {
            y2 = (minworld.getThis() ? -64 : 0);
        }
        if (y1 > 255) {
            y1 = 255;
        }
        Shadow.c.player.sendChatMessage("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " minecraft:" + material.getThis());
    }
}
