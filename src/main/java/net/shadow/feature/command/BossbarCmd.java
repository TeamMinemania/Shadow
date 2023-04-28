package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

public class BossbarCmd extends Command {
    public BossbarCmd() {
        super("bossbar", "creates named bossbars");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"server trashed by moles"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("you must be in creative to use this");
            return;
        }

        if (args.length == 0) {
            ChatUtils.message("Please use the format >bossbar <name>");
            return;
        }

        String message = String.join(" ", args);
        message = message.replace("&", "\u00a7").replace("\u00a7\u00a7", "$");

        Item bossbaritem = Registry.ITEM.get(new Identifier("wither_skeleton_spawn_egg"));
        ItemStack stack = new ItemStack(bossbaritem, 1);
        stack.setNbt(createNBT());
        stack.setCustomName(new LiteralText(message));
        placeStackInHotbar(stack);
        ChatUtils.message("Created Bossbar With Name\"" + message + "\u00a7r\".");
    }

    private NbtCompound createNBT() {
        try {
            return StringNbtReader.parse(
                    "{EntityTag:{id:\"minecraft:wither\",Silent:1b,Invulnerable:1b,NoAI:1b,Invul:882}}");

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean placeStackInHotbar(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (!Shadow.c.player.getInventory().getStack(i).isEmpty())
                continue;

            Shadow.c.player.networkHandler.sendPacket(
                    new CreativeInventoryActionC2SPacket(36 + i, stack));
            return true;
        }

        return false;
    }
}
