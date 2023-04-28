package net.shadow.feature.module;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class ExplosionModule extends Module implements LeftClick {
    final SliderValue size = this.config.create("Size", 1, 1, 10, 1);

    public ExplosionModule() {
        super("Explosion", "make cool explosions with op", ModuleType.GRIEF);
    }

    private static String getBlockNameFromTranslationKey(String translationkey) {
        return translationkey.replace("block.minecraft.", "");
    }

    private static Double[] getRandoms() {
        Double a = Math.random() + 0.1;
        System.out.println();
        Double b = Math.random() + 0.1;
        double c = (Math.log(a / b + 1) + 1);
        double d = (Math.log(b / a + 1) + 1);
        System.out.println(c + ":" + d + "");
        double e;
        double f;
        if (new Random().nextBoolean()) {
            e = c * 1;
        } else {
            e = c * -1;
        }
        if (new Random().nextBoolean()) {
            f = d * 1;
        } else {
            f = d * -1;
        }
        return new Double[]{e / 4, f / 4};
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(LeftClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
        BlockPos hit = new BlockPos(blockHitResult.getBlockPos());
        for (int x = -10; x < 10; x++)
            for (int y = -10; y < 10; y++)
                for (int z = -10; z < 10; z++) {
                    BlockPos pos = hit.add(new BlockPos(x, y, z));
                    if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(new Vec3d(hit.getX(), hit.getY(), hit.getZ())) > size.getThis() || Shadow.c.world.getBlockState(pos).isAir())
                        continue;
                    BlockState s = Shadow.c.world.getBlockState(pos);
                    Shadow.c.player.sendChatMessage("/setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " minecraft:air");
                    Double[] c = getRandoms();
                    Shadow.c.player.sendChatMessage("/summon falling_block " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " {BlockState:{Name:\"minecraft:" + getBlockNameFromTranslationKey(s.getBlock().getTranslationKey()) + "\"},Time:1,Motion:[" + c[0] + ",1.0," + c[1] + "]}");
                }
    }
}
