package net.shadow.feature.module;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.Utils;

public class AnnihilatorModule extends Module implements LeftClick {
    final BooleanValue fancy = this.config.create("FeLines", false);
    final BooleanValue rapid = this.config.create("Rapid", false);
    final BooleanValue minworld = this.config.create("LowWorldDepth", false);
    final SliderValue v = this.config.create("Size", 10, 1, 15, 1);
    final CustomValue<String> m = this.config.create("Material", "lava");

    public AnnihilatorModule() {
        super("Annihilator", "Destroy vast amounts of blocks", ModuleType.GRIEF);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + m.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.c.player.sendChatMessage("/gamerule sendCommandFeedback false");
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.c.player.sendChatMessage("/gamerule sendCommandFeedback true");
    }

    @Override
    public void onUpdate() {
        if (rapid.getThis() && Shadow.c.options.attackKey.isPressed()) {
            String sizetostring = v.getThis().toString();
            BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
            BlockPos pos = new BlockPos(blockHitResult.getBlockPos());
            fillFrom(pos.getX(), pos.getY(), pos.getZ(), (int) Math.round(v.getThis()));
            if (fancy.getThis()) {
                Utils.drawLine(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), "minecraft:flame", 100);
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onLeftClick(LeftClickEvent l) {
        String sizetostring = v.getThis().toString();
        BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
        BlockPos pos = new BlockPos(blockHitResult.getBlockPos());
        fillFrom(pos.getX(), pos.getY(), pos.getZ(), (int) Math.round(v.getThis()));
        if (fancy.getThis()) {
            Utils.drawLine(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), "minecraft:flame", 100);
        }
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
        Shadow.c.player.sendChatMessage("/fill " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " minecraft:" + m.getThis());
    }
}
