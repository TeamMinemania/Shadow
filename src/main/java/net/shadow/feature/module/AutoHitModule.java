package net.shadow.feature.module;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

public class AutoHitModule extends Module {
    public AutoHitModule() {
        super("AutoHit", "automatically hit infront", ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (!(Shadow.c.crosshairTarget instanceof EntityHitResult)) return;
        if (Shadow.c.player.getAttackCooldownProgress(0) < 1) return;

        Entity target = ((EntityHitResult) Shadow.c.crosshairTarget).getEntity();
        Shadow.c.interactionManager.attackEntity(Shadow.c.player, target);
        Shadow.c.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onRender() {

    }
}
