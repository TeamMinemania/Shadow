package net.shadow.feature.command;

import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Arrays;

public class PeekCmd extends Command implements RenderListener {

    private static final ItemStack[] ITEMS = new ItemStack[27];

    public PeekCmd() {
        super("peek", "look inside shulker boxes");
    }

    private static void getItemsInContainerItem(ItemStack itemStack, ItemStack[] items) {
        Arrays.fill(items, ItemStack.EMPTY);
        NbtCompound nbt = itemStack.getNbt();

        if (nbt != null && nbt.contains("BlockEntityTag")) {
            NbtCompound nbt2 = nbt.getCompound("BlockEntityTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    items[nbt3.getCompound(i).getByte("Slot")] = ItemStack.fromNbt(nbt3.getCompound(i));
                }
            }
        }
    }

    @Override
    public void call(String[] args) {
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        ItemStack stack = Shadow.c.player.getMainHandStack();
        getItemsInContainerItem(stack, ITEMS);
        ChatUtils.message("Opening shulker box screen");
        Shadow.c.setScreen(new DecorShulkerBoxScreen(new ShulkerBoxScreenHandler(0, Shadow.c.player.getInventory(), new SimpleInventory(ITEMS)), Shadow.c.player.getInventory(), stack.getName()));
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    private static class DecorShulkerBoxScreen extends ShulkerBoxScreen {
        public DecorShulkerBoxScreen(ShulkerBoxScreenHandler handler, PlayerInventory inv, Text title) {
            super(handler, inv, title);
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            return false;
        }

        @Override
        public boolean mouseReleased(double mx, double my, int button) {
            return false;
        }
    }
}
