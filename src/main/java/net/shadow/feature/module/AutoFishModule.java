package net.shadow.feature.module;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Utils;

import java.util.Random;

public class AutoFishModule extends Module implements PacketInput {
    static int delay = 0;
    final SliderValue lazytime = this.config.create("Human", 1, 1, 40, 0);
    BooleanValue strict = this.config.create("Strict", false);

    public AutoFishModule() {
        super("AutoFish", "auto get fishies", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        click();
        delay = 0;
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        int fishingrod = getFishingRod();
        if (fishingrod == -1) {
            this.setEnabled(false);
            ChatUtils.message("No Valid Fishing rod found, disabling.");
            return;
        } else {
            Shadow.c.player.getInventory().selectedSlot = fishingrod;
        }
        delay++;
        if (delay > lazyRoundTime()) {
            delay = 0;
        } else {
            return;
        }
        if (Shadow.c.player.fishHook == null || Shadow.c.player.fishHook.isRemoved()) {
            new Thread(() -> {
                Utils.sleep(lazyRoundTime() * 50L);
                click();
            }).start();
        }
    }

    @Override
    public void onRender() {
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (packet.getSound().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
                new Thread(() -> {
                    Utils.sleep(lazyRoundTime() * 100L);
                    click();
                }).start();
            }
        }
    }

    public int getFishingRod() {
        if (Shadow.c.player.getMainHandStack().getItem().equals(Items.FISHING_ROD)) {
            return Shadow.c.player.getInventory().selectedSlot;
        }
        for (int i = 0; i < 9; i++) {
            if (Shadow.c.player.getInventory().getStack(36 + i).getItem().equals(Items.FISHING_ROD)) {
                return i;
            }
        }
        return -1;
    }

    private void click() {
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
    }

    private int lazyRoundTime() {
        return (int) Math.round(lazytime.getThis()) + new Random().nextInt(10);
    }
}
