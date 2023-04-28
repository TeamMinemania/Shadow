package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;

public class FireballCmd extends Command {

    String fireballpower;

    public FireballCmd() {
        super("fireball", "generates custom fireballs");
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            ChatUtils.message("Please use the format >fireball <power>");
            return;
        }

        fireballpower = args[0];
        Item item = Registry.ITEM.get(new Identifier("blaze_spawn_egg"));
        ItemStack stack = new ItemStack(item, 1);
        stack.setNbt(CreativeUtils.parse("{display:{Name:'{\"text\":\"Fireball\",\"color\":\"dark_gray\",\"italic\":false}',Lore:['{\"text\":\"Fireball of power " + fireballpower + "\",\"color\":\"gray\",\"italic\":false}']},EntityTag:{id:\"minecraft:fireball\",ExplosionPower:" + fireballpower + ",direction:[0.0,-1.0,0.0],power:[0.0,-1.0,0.0]}}"));
        CreativeUtils.give(stack);
    }
}
