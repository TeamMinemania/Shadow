package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;

public class HoloCmd extends Command {
    public HoloCmd() {
        super("holo", "create holograms");
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("Please use the format >holo <name>");
            return;
        }

        if (args.length == 0) {
            ChatUtils.message("Please use the format >holo <name>");
            return;
        }

        String message = String.join(" ", args);

        message = message.replace("&", "\u00a7").replace("\u00a7\u00a7", "$");

        Item bossbaritem = Registry.ITEM.get(new Identifier("wither_skeleton_spawn_egg"));
        ItemStack stack = new ItemStack(bossbaritem, 1);
        stack.setNbt(CreativeUtils.parse("{EntityTag:{id:\"minecraft:armor_stand\",CustomNameVisible:1b,NoGravity:1b,Silent:1b,Invulnerable:1b,Invisible:1b}}"));
        stack.setCustomName(new LiteralText(message));
        CreativeUtils.give(stack);
        ChatUtils.message("Created Holo With Name\"" + message + "\u00a7r\".");
    }
}
