package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class SpawnLocCmd extends Command {
    public SpawnLocCmd() {
        super("spawnloc", "set the spawn location of a spawn egg to where you are");
    }

    @Override
    public void call(String[] args) {
        ItemStack item = Shadow.c.player.getMainHandStack();
        NbtCompound a = item.getOrCreateSubNbt("EntityTag");
        NbtList postag = new NbtList();
        postag.add(NbtDouble.of(Shadow.c.player.getX()));
        postag.add(NbtDouble.of(Shadow.c.player.getX()));
        postag.add(NbtDouble.of(Shadow.c.player.getX()));
        a.put("Pos", postag);
        NotificationSystem.notifications.add(new Notification("SpawnLoc", "Set the spawn location!", 150));
    }
}
