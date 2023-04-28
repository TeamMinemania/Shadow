package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
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
import net.shadow.feature.configuration.CustomValue;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.Random;

public class AutoHologramModule extends Module implements RenderListener {

    final CustomValue<String> text = this.config.create("Text", "&cShadow Client on top!");
    BlockPos home = new BlockPos(0, 0, 0);
    BlockPos current = new BlockPos(0, 0, 0);

    public AutoHologramModule() {
        super("AutoHologram", "place holograms randomly", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        home = Shadow.c.player.getBlockPos();
        home = home.offset(Direction.DOWN, 6);
        home = home.offset(Direction.WEST, 6);
        home = home.offset(Direction.NORTH, 6);
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        ItemStack old = Shadow.c.player.getMainHandStack();
        Random rand = new Random();
        int x = home.getX() + rand.nextInt(10);
        int y = home.getY() + rand.nextInt(10);
        int z = home.getZ() + rand.nextInt(10);
        current = new BlockPos(x, y, z);
        ItemStack armor = new ItemStack(Items.ARMOR_STAND, 1);
        try {
            armor.setNbt(StringNbtReader.parse("{EntityTag:{CustomNameVisible:1b,NoGravity:1b,Silent:1b,Invulnerable:1b,Invisible:1b,NoBasePlate:1b,Pos:[" + x + ".5," + y + ".5," + z + ".5],CustomName:'{\"text\":\"" + text.getThis() + "\"}'}}"));
        } catch (Exception ignored) {
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, armor));
        try {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        } catch (Exception ignored) {
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, old));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        Vec3d pos = new Vec3d(home.getX(), home.getY(), home.getZ());
        RenderUtils.renderObject(pos, new Vec3d(1, 1, 1), new Color(50, 50, 50, 100), matrix);
        Vec3d active = new Vec3d(current.getX(), current.getY(), current.getZ());
        RenderUtils.renderObject(active, new Vec3d(1, 2, 1), new Color(100, 100, 100, 100), matrix);
    }
}
