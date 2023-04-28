package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class LinkPlayerCmd extends Command {
    public LinkPlayerCmd() {
        super("linkplayer", "spawn a wolf linked to a player");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode");
            return;
        }
        if (args.length < 1) {
            ChatUtils.message("Incorrect Arguments, use >linkplayer <player>");
            return;
        }
        String player = PlayerUtils.completeName(args[0]);
        if (player.equals("none")) {
            ChatUtils.message("that player does not exist");
            return;
        }
        int[] ub = CreativeUtils.getIntsFromUser(player);
        if (ub == null) {
            ChatUtils.message("that player does not exist");
            return;
        }
        NbtCompound tag = CreativeUtils.parse("{EntityTag:{CustomNameVisible:0b,Owner:[I;" + ub[0] + "," + ub[1] + "," + ub[2] + "," + ub[3] + "],Sitting:1b}}");
        Item item = Registry.ITEM.get(new Identifier("wolf_spawn_egg"));
        ItemStack handitem = Shadow.c.player.getMainHandStack();
        ItemStack stack = new ItemStack(item, 1);
        stack.setNbt(tag);
        CreativeUtils.setSlot(36 + Shadow.c.player.getInventory().selectedSlot, stack);
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        CreativeUtils.setSlot(36 + Shadow.c.player.getInventory().selectedSlot, handitem);
        ChatUtils.message("Spawned linked wolf for " + player);
    }
}
