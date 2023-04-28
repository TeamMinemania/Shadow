package net.shadow.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.event.base.EventHandler;
import net.shadow.event.events.ChatOutput.ChatOutputEvent;
import net.shadow.event.events.PlayerMove.PlayerMoveEvent;
import net.shadow.event.events.WaterListener.IsPlayerInWaterEvent;
import net.shadow.feature.CommandRegistry;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import net.shadow.feature.module.other.Unload;
import net.shadow.inter.IClientPlayerEntity;
import net.shadow.plugin.GlobalConfig;
import net.shadow.scripting.Executor;
import net.shadow.utils.ChatUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPlayerEntity.class)
public class ChatOutputMixin extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Shadow
    @Final
    protected MinecraftClient client;

    private Screen keepScreen;


    public ChatOutputMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessageSent(String message, CallbackInfo ci) {
        if (!Unload.loaded) return;
        if (message.toLowerCase().startsWith(GlobalConfig.getPrefix())) {
            ci.cancel();
            try {
                String cmd = message.substring(1);
                String[] args = cmd.split(" ");
                Command co = CommandRegistry.find(args[0].toLowerCase());
                if (co == null) {
                    if (Executor.commandsTable.containsKey(args[0].toLowerCase())) {
                        Executor.memoryTable.put("args", String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                        Executor.exec(Executor.commandsTable.get(args[0].toLowerCase()));
                        return;
                    }
                    ChatUtils.message("Unknown command, use >help");
                    return;
                }
                args = Arrays.copyOfRange(args, 1, args.length);
                co.call(args);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (message.startsWith(",") && net.shadow.Shadow.prismaSocket != null) {
            // IRC
            net.shadow.Shadow.prismaSocket.sendMessage(message.substring(1));
            ci.cancel();
        }
        ChatOutputEvent event = new ChatOutputEvent(message);
        EventHandler.call(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        System.out.println(event.isModified());
        if (!event.isModified())
            return;

        ChatMessageC2SPacket packet =
                new ChatMessageC2SPacket(event.getMessage());
        MinecraftClient.getInstance().player.networkHandler.sendPacket(packet);
        ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"})
    private void onMove(MovementType type, Vec3d offset, CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent(this);
        EventHandler.call(event);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "pushOutOfBlocks")
    public void onPushOutOfBlocks(double var1, double var2, CallbackInfo ci) {
        if (ModuleRegistry.find("NoClip").isEnabled() || ModuleRegistry.find("Freecam").isEnabled()) {
            ci.cancel();
        }
    }


    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
	private void sendMovementPackets(CallbackInfo ci) {
        if(ModuleRegistry.find("PacketFly").isEnabled()){
            net.shadow.Shadow.c.player.setVelocity(Vec3d.ZERO);
            ci.cancel();
        }
	}

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void move(MovementType type, Vec3d movement, CallbackInfo ci) {
        if(ModuleRegistry.find("PacketFly").isEnabled()){
            ci.cancel();
        }
    }

    @Override
    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
    }

    @Override
    public void setMovementMultiplier(Vec3d movementMultiplier) {

    }

    @Override
    public boolean isTouchingWater() {
        boolean inWater = super.isTouchingWater();
        IsPlayerInWaterEvent event = new IsPlayerInWaterEvent(inWater);
        EventHandler.call(event);

        return event.isInWater();
    }


    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.GETFIELD, ordinal = 0), method = {"updateNausea()V"})
    private void beforeUpdateNausea(CallbackInfo ci) {
        if (!ModuleRegistry.find("Portals").isEnabled())
            return;

        keepScreen = client.currentScreen;
        client.currentScreen = null;
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;nextNauseaStrength:F", opcode = Opcodes.GETFIELD, ordinal = 1), method = {"updateNausea()V"})
    private void afterUpdateNausea(CallbackInfo ci) {
        if (keepScreen != null) {
            MinecraftClient.getInstance().currentScreen = keepScreen;
            keepScreen = null;
        }
    }
}