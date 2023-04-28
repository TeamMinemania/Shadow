package net.shadow.feature.module;

import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

import java.util.Arrays;

public class PacketLoggerModule extends Module implements PacketInput, PacketOutput {

    final BooleanValue output = this.config.create("View Output", false);
    final BooleanValue input = this.config.create("View Input", false);

    public PacketLoggerModule() {
        super("PacketLogger", "log packets", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
        System.out.println("I AM LOGGING the real");
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
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
        if (output.getThis()) System.out.println(event.getPacket().toString());
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (isBad(event.getPacket().toString()) && input.getThis())
            System.out.println(event.getPacket().toString());
        CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) event.getPacket();
        System.out.println(packet.getChannel());
        System.out.println(Arrays.toString(packet.getData().getWrittenBytes()));
    }


    private boolean isBad(String bad) {
        return bad.contains("CustomPayload");
    }
}
