package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookSpoofModule extends Module {
    final CustomValue<String> title = this.config.create("Title", "hey");
    final CustomValue<String> author = this.config.create("Author", "the_j");

    public BookSpoofModule() {
        super("BookSpoof", "tries to hack a book", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
        if (Shadow.c.player.getMainHandStack().getItem().equals(Items.WRITTEN_BOOK)) {
            ItemStack book = Shadow.c.player.getMainHandStack();
            List<String> pages = new ArrayList<>();
            book.getNbt().put("author", NbtString.of(author.getThis()));
            book.getNbt().put("title", NbtString.of(title.getThis()));
            Optional<String> titles = Optional.of(title.getThis());
            try {
                NbtList page = (NbtList) book.getNbt().get("pages");
                for (NbtElement item : page) {
                    pages.add(((NbtCompound) item).get("text").asString());
                }
            } catch (Exception e) {
                NotificationSystem.notifications.add(new Notification("BookSpoof", "Invalid Book", 150));
                return;
            }
            Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, pages, titles));
            NotificationSystem.notifications.add(new Notification("BookSpoof", "BookSpoof Completed Successfully", 150));
        } else {
            NotificationSystem.notifications.add(new Notification("BookSpoof", "You arent holding a book!", 150));
        }
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
