package net.shadow.feature.module;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class GravatizerModule extends Module {
    final SliderValue rad = this.config.create("Radius", 1, 3, 20, 0);

    public GravatizerModule() {
        super("Gravatizer", "makes block obey gravity", ModuleType.GRIEF);
    }

    private static String getBlockNameFromTranslationKey(String translationkey) {
        return translationkey.replace("block.minecraft.", "");
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        for (int x = -20; x < 20; x++)
            for (int y = -20; y < 20; y++)
                for (int z = -20; z < 20; z++) {
                    BlockPos p = Shadow.c.player.getBlockPos();
                    BlockPos pos = p.add(new BlockPos(x, y, z));
                    if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(new Vec3d(p.getX(), p.getY(), p.getZ())) > rad.getThis() || Shadow.c.world.getBlockState(pos).isAir() || !Shadow.c.world.getBlockState(pos.offset(Direction.DOWN, 1)).isAir())
                        continue;
                    BlockState s = Shadow.c.world.getBlockState(pos);
                    Shadow.c.player.sendChatMessage("/setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " minecraft:air");
                    Shadow.c.player.sendChatMessage("/summon falling_block " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " {BlockState:{Name:\"minecraft:" + getBlockNameFromTranslationKey(s.getBlock().getTranslationKey()) + "\"},Time:1}");
                }
    }

    @Override
    public void onRender() {

    }
}
