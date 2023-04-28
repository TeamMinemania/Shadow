package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;

public class CoordsPointerModule extends Module implements RenderListener {

    final CustomValue<String> vbo = this.config.create("Coords", "0 100 0");

    public CoordsPointerModule() {
        super("Waypoint", "highlight some coords", ModuleType.RENDER);
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
        String[] minced = vbo.getThis().split(" ");
        if (minced.length < 2) return;
        int x = Integer.parseInt(minced[0]);
        int y = Integer.parseInt(minced[1]);
        int z = Integer.parseInt(minced[2]);
        BlockPos pos = new BlockPos(x,y,z);
        RenderUtils.renderLineToCoords(matrix,pos,new Color(100, 100, 100, 255));
        RenderUtils.renderBlock(matrix,pos, new Color(100, 100, 100, 100));

    }
}
