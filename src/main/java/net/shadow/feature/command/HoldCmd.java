package net.shadow.feature.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.PlayerUtils;

public class HoldCmd extends Command {
    public HoldCmd() {
        super("hold", "make someone hold something");
    }

    @Override
    public void call(String[] args) {
        ItemStack item = new ItemStack(Registry.ITEM.get(new Identifier(args[1])), 1);
        Entity ent = PlayerUtils.getEntity(args[0]);
        ent.equipStack(EquipmentSlot.MAINHAND, item);
    }
}
