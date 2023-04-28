package net.shadow.feature.module;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.plugin.CustomStacksPlugin;

import java.util.List;

public class NBTLoggerModule extends Module implements PacketInput {

    BooleanValue logitems = this.config.create("Items", true);

    public NBTLoggerModule() {
        super("NBTLogger", "log nbt", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof EntityEquipmentUpdateS2CPacket packet) {
            Entity packetentity = Shadow.c.world.getEntityById(packet.getId());
            if (!(packetentity instanceof PlayerEntity)) return;
            List<Pair<EquipmentSlot, ItemStack>> pairs = packet.getEquipmentList();
            for (Pair<EquipmentSlot, ItemStack> pair : pairs) {
                ItemStack i = pair.getSecond();
                if (!i.hasNbt()) return;
                if (!CustomStacksPlugin.has(i)) {
                    CustomStacksPlugin.add(i);
                }
            }
        }

    }
}
