package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Arrays;

public class OpenCmd extends Command {

    private static final ItemStack[] ITEMS = new ItemStack[27];

    public OpenCmd() {
        super("open", "open containers");
    }

    private static void getItemsInContainerItem(ItemStack itemStack, ItemStack[] items) {
        Arrays.fill(items, ItemStack.EMPTY);
        NbtCompound nbt = itemStack.getNbt();

        if (nbt != null && nbt.contains("ShadowItemTag")) {
            NbtCompound nbt2 = nbt.getCompound("ShadowItemTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(
                            new CreativeInventoryActionC2SPacket(nbt3.getCompound(i).getByte("Slot") + 9, ItemStack.fromNbt(nbt3.getCompound(i))));
                }
            }
        }
        if (nbt != null && nbt.contains("BlockEntityTag")) {
            NbtCompound nbt2 = nbt.getCompound("BlockEntityTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(
                            new CreativeInventoryActionC2SPacket(nbt3.getCompound(i).getByte("Slot") + 9, ItemStack.fromNbt(nbt3.getCompound(i))));
                }
            }
        }
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message("Done!");
        getItemsInContainerItem(Shadow.c.player.getMainHandStack(), ITEMS);
    }
}
