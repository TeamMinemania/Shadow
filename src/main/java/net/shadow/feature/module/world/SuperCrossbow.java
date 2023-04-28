package net.shadow.feature.module.world;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;

public class SuperCrossbow extends Module {
    public static final String inbt = "{Enchantments:[{id:\"minecraft:quick_charge\",lvl:5s}],ChargedProjectiles:[{},{id:\"minecraft:arrow\",Count:1b},{}],Charged:1b}";
    private static ItemStack stack;
    final MultiValue mode = this.config.create("Mode", "Creative", "Creative", "Prism");
    ItemStack before = new ItemStack(Registry.ITEM.get(new Identifier("air")), 1);


    public SuperCrossbow() {
        super("SuperCrossbow", "shootz aro", ModuleType.WORLD);
    }

    public static void setProjectile(ItemStack i) {
        stack = i;
    }

    @Override
    public void onEnable() {
        if (stack == null) {
            stack = new ItemStack(Registry.ITEM.get(new Identifier("crossbow")), 1);
            try {
                stack.setNbt(StringNbtReader.parse(inbt));
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (mode.getThis().equalsIgnoreCase("prism")) {
            if (!Shadow.c.options.pickItemKey.isPressed()) return;
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.UP));
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        } else {
            if (!getItemNameFromStack(Shadow.c.player.getMainHandStack()).equals(getItemNameFromStack(stack))) {
                before = Shadow.c.player.getMainHandStack();
            }
            if (Shadow.c.options.pickItemKey.isPressed()) {
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
            }
        }
    }

    private String getItemNameFromStack(ItemStack hstack) {
        String hs = hstack.getItem().getTranslationKey();
        hs = hs.replace("minecraft.", "").replace("block.", "").replace("item.", "");
        return hs;
    }

    @Override
    public void onRender() {

    }
}
