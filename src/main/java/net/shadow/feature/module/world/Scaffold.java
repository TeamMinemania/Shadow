package net.shadow.feature.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Keybind;

import java.util.Objects;

public class Scaffold extends Module {
    SliderValue extend = this.config.create("Extend",5,1,10,1);
    public Scaffold() {
        super("Scaffold", "goes like brrt", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        Vec3d ppos = Objects.requireNonNull(Shadow.c.player).getPos().add(0, -1, 0);
        BlockPos bp = new BlockPos(ppos);
        int selIndex = Shadow.c.player.getInventory().selectedSlot;
        if (!(Shadow.c.player.getInventory().getStack(selIndex).getItem() instanceof BlockItem)) {
            for (int i = 0; i < 9; i++) {
                ItemStack is = Shadow.c.player.getInventory().getStack(i);
                if (is.getItem() == Items.AIR) {
                    continue;
                }
                if (is.getItem() instanceof BlockItem) {
                    selIndex = i;
                    break;
                }
            }
        }
        if (Shadow.c.player.getInventory().getStack(selIndex).getItem() != Items.AIR) {
            boolean sneaking = new Keybind(Shadow.c.options.sneakKey.getDefaultKey().getCode()).isPressed();
            if (sneaking) {
                bp = bp.down();
            }
            // fucking multithreading moment
            int finalSelIndex = selIndex;
            BlockPos finalBp = bp;
            Shadow.c.execute(() -> placeBlockWithSlot(finalSelIndex, finalBp));
            if (extend.getPrec() != 0) {
                Vec3d dir1 = Shadow.c.player.getVelocity().multiply(3);
                Vec3d dir = new Vec3d(MathHelper.clamp(dir1.getX(), -1, 1), 0, MathHelper.clamp(dir1.getZ(), -1, 1));
                Vec3d v = ppos;
                for (double i = 0; i < extend.getThis(); i += 0.5) {
                    v = v.add(dir);
                    if (v.distanceTo(Shadow.c.player.getPos()) >= Objects.requireNonNull(Shadow.c.interactionManager).getReachDistance()) {
                        break;
                    }
                    if (sneaking) {
                        v = v.add(0, -1, 0);
                    }
                    BlockPos bp1 = new BlockPos(v);
                    Shadow.c.execute(() -> placeBlockWithSlot(finalSelIndex, bp1));
                }

            }
        }
    }

    @Override
    public void onRender() {

    }


    void placeBlockWithSlot(int s, BlockPos bp) {
        BlockState st = Objects.requireNonNull(Shadow.c.world).getBlockState(bp);
        if (!st.getMaterial().isReplaceable()) {
            return;
        }
        Vec2f py = getPitchYaw(new Vec3d(bp.getX() + .5, bp.getY() + .5, bp.getZ() + .5));
        Shadow.c.player.setPitch(py.x);
        Shadow.c.player.setYaw(py.x);
        int c = Objects.requireNonNull(Shadow.c.player).getInventory().selectedSlot;
        Shadow.c.player.getInventory().selectedSlot = s;
        BlockHitResult bhr = new BlockHitResult(new Vec3d(bp.getX(), bp.getY(), bp.getZ()), Direction.DOWN, bp, false);
        Objects.requireNonNull(Shadow.c.interactionManager).interactBlock(Shadow.c.player, Shadow.c.world, Hand.MAIN_HAND, bhr);
        Shadow.c.player.getInventory().selectedSlot = c;
    }
    public static Vec2f getPitchYaw(Vec3d targetV3) {
        return getPitchYawFromOtherEntity(Objects.requireNonNull(Shadow.c.player).getEyePos(), targetV3);
    }

    public static Vec2f getPitchYawFromOtherEntity(Vec3d eyePos, Vec3d targetV3) {
        double vec = 57.2957763671875;
        Vec3d target = targetV3.subtract(eyePos);
        double square = Math.sqrt(target.x * target.x + target.z * target.z);
        float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(target.y, square) * vec)));
        float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(target.z, target.x) * vec) - 90.0F);
        return new Vec2f(pitch, yaw);
    }


}
