package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class NbtBypasserModule extends Module implements PacketOutput {
    final BooleanValue reset = this.config.create("Overwrite", false);

    public NbtBypasserModule() {
        super("NbtBypasser", "bypass some anti nbt, might glitch", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof CreativeInventoryActionC2SPacket packet) {
            ItemStack spoofed = packet.getItemStack();
            if (spoofed.getItem() == Items.AIR) {
                return;
            }
            if (reset.getThis()) {
                Shadow.c.player.networkHandler.getConnection().send(new CreativeInventoryActionC2SPacket(36, new ItemStack(Items.AIR, 0)));
                if (Shadow.c.player.getMainHandStack().getItem() != Items.AIR) {
                    Shadow.c.player.networkHandler.getConnection().send(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.AIR, 0)));
                }
            }
            spoofed.setCount(1);
            event.cancel();
            Shadow.c.player.networkHandler.getConnection().send(new CreativeInventoryActionC2SPacket(-999, spoofed));
        }
    }
}
