package net.shadow.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(InventoryS2CPacket.class)
public interface InventoryS2CPacketAccessor {
    @Accessor("contents")
    void setContents(List<ItemStack> contents);

    @Accessor("cursorStack")
    void setCStack(ItemStack contents);
}