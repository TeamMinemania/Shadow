package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.gui.NBTEditGui;


public class OtherScreenModule extends Module implements RenderListener {
    public OtherScreenModule() {
        super("NBTEdit", "open nbt editor", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (!Shadow.c.player.getMainHandStack().hasNbt()) {
            Shadow.c.player.getMainHandStack().getOrCreateNbt();
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, Shadow.c.player.getMainHandStack()));
        }
        Shadow.c.setScreen(new NBTEditGui(Shadow.c.player.getMainHandStack()));
        this.setEnabled(false);
    }
}
