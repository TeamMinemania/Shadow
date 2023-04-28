package net.shadow.feature.module.grief;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;

public class PulseWavemaker extends Module implements RenderListener {
    final ArrayList<BlockPos> targets = new ArrayList<>();
    final SliderValue power = this.config.create("Power", 25, 1, 100, 1);
    int ticks = 95;
    int current = 0;


    public PulseWavemaker() {
        super("PulseWavermaker", "objective: destroy", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        for (int x = -7; x < 8; x++)
            for (int z = -7; z < 8; z++) {
                BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x * 16, 0, z * 16));
                targets.add(pos);
            }

        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
        targets.clear();
    }

    @Override
    public void onUpdate() {
        try {
            Utils.twice(() -> {
                current++;
                if (current > targets.size() - 1) current = 0;
                BlockPos pos = targets.get(current);
                ItemStack fire = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                try {
                    fire.setNbt(StringNbtReader.parse("{EntityTag:{Pos:[" + x + ".5," + y + ".5," + z + ".5],ExplosionPower:" + power.getThis() + ",direction:[0.0d,-1.0d,0.0d],id:\"minecraft:fireball\",power:[0.0d,-1.0d,0.0d]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, fire));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, ((BlockHitResult) Shadow.c.crosshairTarget)));
            });
        } catch (ArrayIndexOutOfBoundsException e) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (BlockPos render : targets) {
            Vec3d pos = new Vec3d(render.getX(), render.getY(), render.getZ());
            RenderUtils.renderObject(pos, new Vec3d(1, 1, 1), new Color(175, 175, 175, 125), matrix);
        }
    }
}
