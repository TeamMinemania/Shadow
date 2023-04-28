package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ColorBooksModule extends Module implements PacketOutput {
    private static final boolean cansend = false;

    public ColorBooksModule() {
        super("ColorBooks", "sign books with color codes", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof BookUpdateC2SPacket packet) {
            int slot = packet.getSlot();
            Optional<String> titles = packet.getTitle();
            List<String> pagess = packet.getPages();
            List<String> fpages = new ArrayList<>();
            for (String s : pagess) {
                String format = s.replace("&", "\u00a7");
                fpages.add(format);
            }
            String formattabletitle = titles.get();
            formattabletitle = formattabletitle.replace("&", "\u00a7");
            titles = Optional.of(formattabletitle);
            System.out.println("tested");
            System.out.println(titles.get());
            for (String s : pagess) {
                System.out.println(s);
            }
            ChatUtils.message("\u00a77[\u00a78ColorBooks\u00a77] Signed Book With Colors");
            Shadow.c.player.networkHandler.getConnection().send(new BookUpdateC2SPacket(slot, fpages, titles));
            event.cancel();
        }
    }
}
