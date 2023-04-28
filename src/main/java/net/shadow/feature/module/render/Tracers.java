package net.shadow.feature.module.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;

public class Tracers extends Module implements RenderListener {
    final BooleanValue players = this.config.create("Players", true);
    final BooleanValue hostile = this.config.create("Hostile", false);
    final BooleanValue passive = this.config.create("Passive", false);
    final BooleanValue misc = this.config.create("Misc", true);
    final SliderValue red = this.config.create("R", 100, 1, 255, 1);
    final SliderValue green = this.config.create("G", 100, 1, 255, 1);
    final SliderValue blue = this.config.create("B", 100, 1, 255, 1);

    public Tracers() {
        super("Tracers", "render lines to players", ModuleType.RENDER);
    }

    //ispassive and ishostile methods from ares client!
    public static boolean isPassive(Entity entity) {
        if (entity instanceof IronGolemEntity && ((IronGolemEntity) entity).getAngryAt() == null) return true;
        else if (entity instanceof WolfEntity && (!((WolfEntity) entity).isAttacking() || ((WolfEntity) entity).getOwner() == Shadow.c.player))
            return true;
        else return entity instanceof AmbientEntity || entity instanceof PassiveEntity || entity instanceof SquidEntity;
    }

    public static boolean isHostile(Entity entity) {
        if (entity instanceof IronGolemEntity)
            return ((IronGolemEntity) entity).getAngryAt() == Shadow.c.player.getUuid() && ((IronGolemEntity) entity).getAngryAt() != null;
        else if (entity instanceof WolfEntity)
            return ((WolfEntity) entity).isAttacking() && ((WolfEntity) entity).getOwner() != Shadow.c.player;
        else if (entity instanceof PiglinEntity) return ((PiglinEntity) entity).isAngryAt(Shadow.c.player);
        else if (entity instanceof EndermanEntity) return ((EndermanEntity) entity).isAngry();
        return entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
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
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            ArrayList<Entity> renders = getTargets();

            for (Entity entity : renders) {
                Vec3d eSource = new Vec3d(MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevX, entity.getX()), MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevY, entity.getY()), MathHelper.lerp(Shadow.c.getTickDelta(), entity.prevZ, entity.getZ()));
                RenderUtils.vector(Utils.RenderCameraStart(), eSource.add(0, entity.getBoundingBox().getYLength() / 2, 0), new Color((int) Math.round(red.getThis()), (int) Math.round(green.getThis()), (int) Math.round(blue.getThis()), 255), matrix, 2);
            }
        });
    }

    public ArrayList<Entity> getTargets() {
        ArrayList<Entity> targets = new ArrayList<>();

        for (Entity ent : Shadow.c.world.getEntities()) {
            if (ent instanceof PlayerEntity player && players.getThis()) {
                if (player == Shadow.c.player) continue;
                targets.add(ent);
            }
            if (isHostile(ent) && hostile.getThis()) {
                targets.add(ent);
            }
            if (isPassive(ent) && passive.getThis()) {
                targets.add(ent);
            }
            if (misc.getThis() && !isPassive(ent) && !isHostile(ent)) {
                targets.add(ent);
            }
        }
        return targets;
    }
}
