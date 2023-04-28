package net.shadow.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class BSBDupe extends Module {
    private final int rows = 3;
    final SliderValue delay = this.config.create("Delay",150,0,1000,0);
    final SliderValue clickDelay = this.config.create("Click Delay",1000,0,10000,0);
    public BSBDupe() {
        super("BSBDupe","BetterShulkerBoxes Dupe", ModuleType.EXPLOIT);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            try {

                MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND);
                DefaultedList<Slot> slots = Shadow.c.player.currentScreenHandler.slots;
                for(int i = 0; i < rows * 9; i++) {
                    Slot slot = slots.get(i);
                    Thread.sleep(delay.getThis().intValue());
                    Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId,slot.id,0, SlotActionType.QUICK_MOVE,Shadow.c.player);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, new BlockPos(0, 0, 0), Direction.UP));
                }
                Thread.sleep(clickDelay.getThis().intValue());
                MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND);
                MinecraftClient.getInstance().player.jump();
                this.toggle();
                NotificationSystem.notifications.add(new Notification("BSBDupe","Successfully duped",500));
            } catch (InterruptedException e) {

            }

        }).start();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRender() {

    }
}
