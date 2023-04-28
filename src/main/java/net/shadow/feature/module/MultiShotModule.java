package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.Random;

public class MultiShotModule extends Module implements RenderListener {
    final SliderValue slider = this.config.create("Amount", 1, 1, 20, 0);

    public MultiShotModule() {
        super("MultiShot", "Shoot Entity Multi??", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        if (Shadow.c.options.useKey.isPressed()) {
            for (int i = 0; i < slider.getThis(); i++) {
                fire();
            }
        }
    }

    @Override
    public void onRender() {

    }

    private void fire() {
        Random r = new Random();
        int ox = r.nextInt(21) - 10;
        int oz = r.nextInt(21) - 10;
        Vec3d vel = Shadow.c.player.getRotationVector().normalize().multiply(3);
        Vec3d spawnPos = Utils.relativeToAbsolute(Shadow.c.player.getCameraPosVec(Shadow.c.getTickDelta()), Shadow.c.player.getRotationClient(), new Vec3d(ox, oz, 0));
        ItemStack spawnEgg = Shadow.c.player.getMainHandStack();
        if (!(spawnEgg.getItem() instanceof SpawnEggItem)) return;
        NbtCompound entityTag = spawnEgg.getOrCreateSubNbt("EntityTag");
        NbtList pos = new NbtList();
        pos.add(NbtDouble.of(spawnPos.x));
        pos.add(NbtDouble.of(spawnPos.y));
        pos.add(NbtDouble.of(spawnPos.z));
        entityTag.put("Pos", pos);
        NbtList motion = new NbtList();
        motion.add(NbtDouble.of(vel.x));
        motion.add(NbtDouble.of(vel.y));
        motion.add(NbtDouble.of(vel.z));
        entityTag.put("Motion", motion);
        entityTag.put("NoGravity", NbtByte.of(true));
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, spawnEgg));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Shadow.c.player.getPos(), Direction.UP, new BlockPos(Shadow.c.player.getPos()), false)));
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (!(Shadow.c.player.getMainHandStack().getItem() instanceof SpawnEggItem)) return;
        for (int ox = -10; ox < 11; ox++) {
            for (int oz = -10; oz < 11; oz++) {
                Vec3d cRot = Shadow.c.player.getRotationVector();
                Vec3d a = Utils.relativeToAbsolute(Shadow.c.player.getCameraPosVec(Shadow.c.getTickDelta()), Shadow.c.player.getRotationClient(), new Vec3d(ox, oz, 0));
                RaycastContext rc = new RaycastContext(a, a.add(cRot.multiply(200)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, Shadow.c.player);
                BlockHitResult bhr = Shadow.c.world.raycast(rc);
                Vec3d p = bhr.getPos();
                RenderUtils.renderObject(p.subtract(.1, .1, .1), new Vec3d(.2, .2, .2), Color.GRAY, matrix);
            }
        }
    }
}
