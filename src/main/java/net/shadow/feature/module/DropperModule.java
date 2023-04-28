package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class DropperModule extends Module {

    final SliderValue sped = this.config.create("Speed", 2, 1, 30, 1);
    private final Random r = new Random();

    public DropperModule() {
        super("Dropper", "Drop items in creative", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < sped.getThis(); i++) {
            //ItemStack j = new ItemStack(Registry.ITEM.getRandom(r), 1);
            ItemStack j = new ItemStack(Items.ACACIA_LOG, 64);
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, j));
            Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.DROP_ALL_ITEMS, new BlockPos(0, 0, 0), Direction.UP));
        }
    }


    //topdogger

    @Override
    public void onRender() {

    }
}
