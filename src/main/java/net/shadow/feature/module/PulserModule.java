package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
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
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.RaycastPlugin;

public class PulserModule extends Module implements RightClick {
    private static ItemStack normal;
    final MultiValue mode = this.config.create("Mode", "Raycasting", "Raycasting", "Shoot");
    final SliderValue topkek = this.config.create("Power", 25, 10, 127, 1);
    final BooleanValue raycast = this.config.create("Raycast", false);

    public PulserModule() {
        super("Fireballs", "shoot fireballs", ModuleType.GRIEF);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RightClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RightClick.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRightClick(RightClickEvent event) {
        if (mode.getThis().equalsIgnoreCase("Shoot")) {
            ClientPlayerEntity player = Shadow.c.player;
            if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                normal = Shadow.c.player.getMainHandStack();
            }
            ItemStack fireball = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
            Vec3d shootvector = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
            try {
                fireball.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:fireball\",ExplosionPower:" + (int) Math.round(topkek.getThis()) + "b,power:[" + shootvector.getX() + "," + shootvector.getY() + "," + shootvector.getZ() + "]}}"));
            } catch (Exception ignored) {
            }
            Shadow.c.player.swingHand(Hand.MAIN_HAND);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball));
            try {
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
            } catch (Exception ignored) {
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
        } else {
            ClientPlayerEntity player = Shadow.c.player;
            if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                normal = Shadow.c.player.getMainHandStack();
            }
            ItemStack fireball = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
            BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
            BlockPos dest = blockHitResult.getBlockPos();
            try {
                fireball.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:fireball\",ExplosionPower:" + (int) Math.round(topkek.getThis()) + "b,Pos:[" + dest.getX() + ".0," + dest.getY() + ".0," + dest.getZ() + ".0],power:[0.0,-1.0,0.0]}}"));
            } catch (Exception ignored) {
            }
            Shadow.c.player.swingHand(Hand.MAIN_HAND);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball));
            try {
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
            } catch (Exception ignored) {
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
            if (!raycast.getThis()) return;
            RaycastPlugin.raycast(dest);
        }
    }
}
