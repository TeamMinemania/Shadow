//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.mixin;


import io.netty.buffer.Unpooled;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.PacketInput.PacketInputEvent;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.ClientSpoofModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class NetworkHandlerMixin
        extends SimpleChannelInboundHandler<Packet<?>> {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        PacketInputEvent event = new PacketInputEvent(packet);
        EventHandler.call(event);

        if (event.isCancelled())
            ci.cancel();
    }

    @ModifyVariable(
            method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V",
            at = @At("HEAD"), argsOnly = true)
    public Packet<?> onSendPacket(Packet<?> p) {
        if (ModuleRegistry.find("ClientSpoof").isEnabled()) {
            if (p instanceof CustomPayloadC2SPacketAccessor packet) {
                if (packet.getChannel().getNamespace().equals("minecraft") && packet.getChannel().getPath().equals("brand")) {
                    return new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientSpoofModule.getBrand()));
                }
            }
        }
        return p;
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {
                    "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"},
            cancellable = true)
    private void onSendPacket(Packet<?> pack,
                              GenericFutureListener<? extends Future<? super Void>> callback,
                              CallbackInfo ci) {
        if (ModuleRegistry.find("ClientSpoof").isEnabled()) {
            if (pack instanceof CustomPayloadC2SPacketAccessor packet) {
                if (packet.getChannel().getNamespace().equals("minecraft") && packet.getChannel().getPath().equals("register"))
                    ci.cancel();
                if (packet.getChannel().getNamespace().equals("fabric")) ci.cancel();
            }
        }
    }


}
