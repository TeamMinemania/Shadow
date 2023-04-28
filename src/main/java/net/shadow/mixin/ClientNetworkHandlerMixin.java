package net.shadow.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.PacketOutput.PacketOutputEvent;
import net.shadow.plugin.BetterItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin implements ClientPlayPacketListener {
    @Inject(at = {@At("HEAD")},
            method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
            cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        PacketOutputEvent event = new PacketOutputEvent(packet);
        EventHandler.call(event);
        BetterItems.onSentPacket(event);
        if (event.isCancelled())
            ci.cancel();
    }

    //i hate you meteor client
    //you cost me 1h of debugging this shit
}
