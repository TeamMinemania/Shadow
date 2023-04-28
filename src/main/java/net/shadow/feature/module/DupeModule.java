package net.shadow.feature.module;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class DupeModule extends Module implements PacketInput {
    private static final long lol = 0;
    static boolean docrafting = false;
    final MultiValue mode = this.config.create("Mode", "Old [1.11]", "Old [1.11]", "Book [1.17]", "RandomBook [1.14]", "Crafting [1.17.0]", "TickPlace [1.17.1]");
    final SliderValue millis = this.config.create("Millis", 1, 60, 200, 1);

    public DupeModule() {
        super("Dupe", "duplicate items", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        String[] p = mode.getThis().split(" ");
        return this.getName() + " [" + p[0] + "]";
    }

    @Override
    public void onEnable() {
        switch (mode.getThis().toLowerCase()) {
            case "old [1.11]" -> {
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, new BlockPos(0, 0, 0), Direction.UP));
                Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("\u00a7f"));
                setEnabled(false);
            }
            case "book [1.17]" -> {
                if (Shadow.c.player.getInventory().getMainHandStack().getItem() != Items.WRITABLE_BOOK) {
                    ChatUtils.message("hold a book and quill in your main hand");
                    this.setEnabled(false);
                    break;
                }
                String listTag = "";
                StringBuilder builder1 = new StringBuilder();
                builder1.append(String.valueOf((char) 2077).repeat(21845));
                listTag.concat(builder1.toString());
                StringBuilder builder2 = new StringBuilder();
                builder2.append("aaaaaaaa".repeat(32));
                String string2 = builder2.toString();
                for (int i = 1; i < 40; i++)
                    listTag.concat(string2);
                List<String> title = List.of("The Dupe is patched on this server");
                Optional<String> pages = Optional.of(listTag);
                Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, title, pages));
                setEnabled(false);
            }
            case "randombook [1.14]" -> {
                String texts = "";
                IntStream chars = new Random().ints(0, 0x10FFFF + 1);
                SplittableRandom random = new SplittableRandom();
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < 210 * 210; i++) {
                    s.append((char) random.nextInt(Integer.MAX_VALUE));
                }
                String text = s.toString();
                for (int t = 0; t < 100; t++) {
                    texts.concat(text.substring(t * 210, (t + 1) * 210));
                }
                List<String> title2 = List.of(texts);
                Optional<String> pages2 = Optional.of("The Dupe is patched on this server");
                Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, title2, pages2));
                setEnabled(false);
            }
            case "tickplace [1.17.1]" -> {
                new Thread(() -> {
                    Shadow.c.player.sendChatMessage("\u00a7f");
                    try {
                        Thread.sleep(Long.parseLong(millis.getThis() + ""));
                    } catch (NumberFormatException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                }).start();
                this.setEnabled(false);
            }
        }
        if (mode.getThis().equalsIgnoreCase("crafting [1.17.0]")) {
            docrafting = true;
        }
    }

    @Override
    public void onDisable() {
        docrafting = false;
    }

    @Override
    public void onUpdate() {
        docrafting = mode.getThis().equalsIgnoreCase("Crafting [1.17.0]");
    }

    @Override
    public void onRender() {

    }

    @Override
    public String getSpecial() {
        if (docrafting) {
            return "true";
        } else {
            return "false";
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof DisconnectS2CPacket) {
            System.out.println((System.currentTimeMillis() - lol));
        }
    }
}
