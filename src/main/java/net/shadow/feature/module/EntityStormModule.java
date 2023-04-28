package net.shadow.feature.module;

import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.LeftClick;
import net.shadow.event.events.MiddleClick;
import net.shadow.event.events.RightClick;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.SliderValue;

import java.util.Random;

public class EntityStormModule extends Module implements RightClick, LeftClick, MiddleClick {
    final SliderValue random2 = this.config.create("Randomness", 1, 1, 10, 0);
    final SliderValue par = this.config.create("Amount", 1, 20, 100, 0);
    final SliderValue limiter = this.config.create("Single", 1, 1, 10, 0);
    final SliderValue speed = this.config.create("Speed", 1, 1, 10, 1);
    final SliderValue displace = this.config.create("Displacement", 1, 1, 20, 1);
    final CustomValue<String> entity = this.config.create("Entity", "arrow");
    final CustomValue<String> NBT = this.config.create("NBT", "");
    final CustomValue<String> NBTE = this.config.create("NBTCLICK", "{}");

    public EntityStormModule() {
        super("EntityStorm", "fire entities", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RightClick.class, this);
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(MiddleClick.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RightClick.class, this);
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(MiddleClick.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        for (int i = 0; i < par.getThis(); i++) {
            Shadow.c.player.sendChatMessage("/summon minecraft:" + entity.getThis() + " ^" + getRandRange() + " ^" + getRandRange() + " ^" + (displace.getThis() - 10) + " {NoGravity:1b,Tags:[controlled]," + NBT.getThis() + "}");
        }
    }

    @Override
    public void onRightClick(RightClickEvent event) {
        Vec3d forward = Vec3d.fromPolar(Shadow.c.player.getPitch(), Shadow.c.player.getYaw()).normalize();
        Shadow.c.player.sendChatMessage("/execute as @e[tag=controlled,sort=random,limit=" + (int) Math.round(limiter.getThis()) + "] run data merge entity @s {Motion:[" + forward.x * speed.getThis() + "," + forward.y * speed.getThis() + "," + forward.z * speed.getThis() + "],Tags:[]," + NBTE.getThis() + "}");
    }

    @Override
    public void onMiddleClick(MiddleClickEvent event) {
        Vec3d forward = Vec3d.fromPolar(Shadow.c.player.getPitch(), Shadow.c.player.getYaw()).normalize();
        Shadow.c.player.sendChatMessage("/execute as @e[tag=controlled] run data merge entity @s {Motion:[" + forward.x * speed.getThis() + "," + forward.y * speed.getThis() + "," + forward.z * speed.getThis() + "],Tags:[]," + NBTE.getThis() + "}");
    }

    private double getRandRange() {
        if (new Random().nextBoolean()) {
            return Math.random() * (int) Math.round(random2.getThis());
        } else {
            return Math.random() * (int) Math.round(random2.getThis()) * -1;
        }
    }
}