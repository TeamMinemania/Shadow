package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class ProjectilesModule extends Module implements RightClick {
    private static ItemStack normal;
    final MultiValue mode = this.config.create("Projectile", "Fireball", "Fireball", "GasWeapon", "Wither", "Jihad");

    public ProjectilesModule() {
        super("Projectiles", "shoot projectiles in gmc", ModuleType.WORLD);
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
        ClientPlayerEntity player = Shadow.c.player;
        switch (mode.getThis().toLowerCase()) {
            case "fireball" -> {
                if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                    normal = Shadow.c.player.getMainHandStack();
                }
                ItemStack fireball = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                Vec3d shootvector = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
                try {
                    fireball.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:fireball\",power:[" + shootvector.getX() + "," + shootvector.getY() + "," + shootvector.getZ() + "]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball));
                try {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
            }
            case "gasweapon" -> {
                if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                    normal = Shadow.c.player.getMainHandStack();
                }
                ItemStack fireball2 = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                Vec3d shootvector2 = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
                try {
                    fireball2.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:dragon_fireball\",power:[" + shootvector2.getX() + "," + shootvector2.getY() + "," + shootvector2.getZ() + "]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball2));
                try {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
            }
            case "wither" -> {
                if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                    normal = Shadow.c.player.getMainHandStack();
                }
                ItemStack fireball3 = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                Vec3d shootvector3 = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
                try {
                    fireball3.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:wither_skull\",power:[" + shootvector3.getX() + "," + shootvector3.getY() + "," + shootvector3.getZ() + "]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball3));
                try {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
            }
            case "jihad" -> {
                if (Shadow.c.player.getMainHandStack().getItem() != Items.BLAZE_SPAWN_EGG) {
                    normal = Shadow.c.player.getMainHandStack();
                }
                ItemStack fireball4 = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                Vec3d shootvector4 = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
                try {
                    fireball4.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:small_fireball\",power:[" + shootvector4.getX() + "," + shootvector4.getY() + "," + shootvector4.getZ() + "]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball4));
                try {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, normal));
            }
        }
    }
}
