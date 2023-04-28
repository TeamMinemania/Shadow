package net.shadow.feature.module.combat;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

import java.util.ArrayList;
import java.util.Random;

public class TeleportAura extends Module {
    public TeleportAura() {
        super("TeleportAura", "teleport around and rapidly hit things", ModuleType.COMBAT);
    }

    public static double[] calculateLookAt(double px, double py, double pz, PlayerEntity me) {
        double dirx = me.getX() - px;
        double diry = me.getY() + me.getEyeHeight(me.getPose()) - py;
        double dirz = me.getZ() - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw, pitch};
    }

    private static int randAbsValue(int absvalue) {
        Random r = new Random();
        if (r.nextBoolean()) {
            return r.nextInt(absvalue);
        } else {
            return (r.nextInt(absvalue) * -1);
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = Shadow.c.player;
        ArrayList<Entity> targetlist = getTargets();
        Random rand = new Random();
        for (Entity target : targetlist) {
            Shadow.c.player.updatePosition(target.getX() + randAbsValue(4), target.getY() + randAbsValue(4), target.getZ() + randAbsValue(4));
            break;
        }

        if (player.getAttackCooldownProgress(0F) < 1) return;

        for (Entity target : targetlist) {
            double[] rotations = calculateLookAt(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5, Shadow.c.player);
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) rotations[0], (float) rotations[1], player.isOnGround()));
            Shadow.c.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
            break;
        }
    }

    @Override
    public void onRender() {

    }

    public double distanceTo(Entity e) {
        float f = (float) (Shadow.c.player.getX() - e.getX());
        float g = (float) (Shadow.c.player.getY() - e.getY());
        float h = (float) (Shadow.c.player.getZ() - e.getZ());
        return Math.sqrt(f * f + g * g + h * h);
    }

    public ArrayList<Entity> getTargets() {
        ArrayList<Entity> targets = new ArrayList<>();

        for (Entity ent : Shadow.c.world.getEntities()) {
            if (ent instanceof PlayerEntity player) {
                if (distanceTo(player) > 10) continue;
                if (player == Shadow.c.player) continue;
                targets.add(ent);
            }
        }
        return targets;
    }
}
