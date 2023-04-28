package net.shadow.feature.module;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class GiveModule extends Module implements PacketOutput {
    CustomValue<String> ip = this.config.create("IP", "urjjj.minehut.com");
    CustomValue<Integer> port = this.config.create("PORT", 25565);

    public GiveModule() {
        super("Try", "random shit", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Notification n = new Notification("REAL notification", "wtf how", 150);
        NotificationSystem.notifications.add(n);
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
        if (event.getPacket() instanceof ClickSlotC2SPacket packet) {
            System.out.println(packet.getActionType() + "");
            System.out.println(packet.getButton() + "");
            System.out.println(packet.getSlot() + "");
        }
    }
}
