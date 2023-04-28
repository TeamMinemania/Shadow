package net.shadow.feature.module.other;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class ScreenSaver extends Module implements PacketOutput {
    static HandledScreen<?> spoof;
    final BooleanValue tryslow = this.config.create("Silent", false);

    public ScreenSaver() {
        super("ScreenSaver", "save chest inventories and stuff [press backslash]", ModuleType.OTHER);
    }

    public static void pingSpoof() {
        if (spoof == null) {
            NotificationSystem.notifications.add(new Notification("Screen Saver", "You Don't Have a saved screen!", 150));
            return;
        }
        NotificationSystem.notifications.add(new Notification("Screen Saver", "Opening the Spoofed Screen", 150));
        Shadow.c.setScreen(spoof);
        Shadow.c.player.currentScreenHandler = spoof.getScreenHandler();
    }

    public static void setSpoof(HandledScreen<?> handledScreen) {
        NotificationSystem.notifications.add(new Notification("Screen Saver", "Set the Spoofed Screen", 150));
        spoof = handledScreen;
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
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            event.cancel();
        }
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet) {
            if (tryslow.getThis()) {
                Shadow.c.getNetworkHandler().getConnection().send(packet);
            }
        }
    }
}
