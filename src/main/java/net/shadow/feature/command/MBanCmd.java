package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;

public class MBanCmd extends Command {
    public MBanCmd() {
        super("pban", "gmc ban unremovable");
    }

    @Override
    public void call(String[] args) {
        String name = PlayerUtils.completeName(args[0]);
        int[] player = CreativeUtils.getIntsFromUser(name);
        if (player == null) {
            ChatUtils.message("Invalid Player");
            return;
        }
        ItemStack ban = new ItemStack(Items.ARMOR_STAND, 1);
        ChatUtils.message("Created Ban Cart for " + name);
        try {
            ban.setNbt(StringNbtReader.parse("{EntityTag:{UUID:[I;" + player[0] + "," + player[1] + "," + player[2] + "," + player[3] + "],ArmorItems:[{},{},{},{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:\"" + name + "\"}}]}}"));
        } catch (Exception ignored) {
        }
        ban.setCustomName(Text.of(name));
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, ban));
    }
}
