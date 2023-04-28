package net.shadow.feature.module.grief;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
//import net.shadow.gui.ShadowScreenIMGUI;
import net.shadow.gui.ShadowScreenIMGUI;

public class GriefGUI extends Module implements RenderListener {
    public GriefGUI() {
        super("GriefGUI", "open the shadow grief screen", ModuleType.GRIEF);
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
        Shadow.c.setScreen(new ShadowScreenIMGUI());
        this.setEnabled(false);
    }
}
