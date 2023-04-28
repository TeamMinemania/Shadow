package net.shadow.feature.module;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookWriterModule extends Module {

    final MultiValue mode = this.config.create("Mode", "Ascii", "Ascii", "Raw", "Unicode", "dupe");
    final SliderValue pagesu = this.config.create("Pages", 20, 1, 100, 1);

    public BookWriterModule() {
        super("BookWriter", "write books with packets", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        if (Shadow.c.player.getMainHandStack().getItem() != Items.WRITABLE_BOOK) {
            ChatUtils.message("Please hold a written book");
            this.setEnabled(false);
            return;
        }

        switch (mode.getThis().toLowerCase()) {
            case "unicode":
                IntStream chars = new Random().ints(0, 0x10FFFF + 1);
                String text = chars.limit(210 * Math.round(pagesu.getThis())).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
                List<String> title2 = new ArrayList<>();
                Optional<String> pages2 = Optional.of("Unicode");

                for (int t = 0; t < pagesu.getThis(); t++) {
                    title2.add(text.substring(t * 210, (t + 1) * 210));
                }

                Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, title2, pages2));
                this.setEnabled(false);
                break;

            case "raw":
                List<String> title = new ArrayList<>();
                for (int i = 0; i < pagesu.getThis(); i++) {
                    StringBuilder page2 = new StringBuilder();
                    page2.append(String.valueOf((char) 2048).repeat(266));
                    title.add(page2.toString());
                }

                Optional<String> pages = Optional.of("Raw");
                Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, title, pages));
                this.setEnabled(false);
                break;


            case "dupe":

                break;

            case "ascii":
                Random r = new Random();
                List<String> title3 = new ArrayList<>();
                for (int i = 0; i < pagesu.getThis(); i++) {
                    StringBuilder page = new StringBuilder();
                    for (int j = 0; j < 266; j++) {
                        page.append((char) r.nextInt(25) + 97);
                    }
                    title3.add(page.toString());
                }


                Optional<String> pages3 = Optional.of("Ascii");
                Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, title3, pages3));
                this.setEnabled(false);
                break;
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
