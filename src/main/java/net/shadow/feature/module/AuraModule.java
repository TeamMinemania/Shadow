package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
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
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class AuraModule extends Module implements PacketOutput, RenderListener {
    private static ArrayList<Entity> targetlist = new ArrayList<>();
    private static double[] rotations = null;
    private static int tickcounter = 0;
    final SliderValue range = this.config.create("Range", 5, 1, 25, 1);
    final MultiValue rotmode = this.config.create("Rotations", "Snap", "Snap", "Stay", "Packet");
    final BooleanValue aplayers = this.config.create("Players", true);
    final BooleanValue hostile = this.config.create("Hostile", false);
    final BooleanValue passive = this.config.create("Passive", false);
    final BooleanValue antibot = this.config.create("Antibot", true);
    Entity tgt = null;


    public Entity getTarget(){
        return tgt;
    }

    public AuraModule() {
        super("Aura", "Automatically target and hit people", ModuleType.COMBAT);
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

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        tickcounter++;
        ClientPlayerEntity player = Shadow.c.player;

        if (Shadow.c.player.getAttackCooldownProgress(0) < 1) return;

        targetlist = getTargets();
        for (Entity target : targetlist) {
            if (rotmode.getThis().equals("Snap") || rotmode.getThis().equalsIgnoreCase("stay")) {
                tgt = target;
                Vec3d center = target.getBoundingBox().getCenter();
                rotations = calculateLookAt(center.x, center.y, center.z, Shadow.c.player);
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(Shadow.c.player.getX(), Shadow.c.player.getY(), Shadow.c.player.getZ(), (float) rotations[0], (float) rotations[1], player.isOnGround()));
                Shadow.c.interactionManager.attackEntity(player, target);
            } else {
                tgt=target;
                Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, false));
            }
            player.swingHand(Hand.MAIN_HAND);
            break;
        }
    }

    @Override
    public void onRender() {

    }

    public ArrayList<Entity> getTargets() {
        ArrayList<Entity> targets = new ArrayList<>();

        for (Entity ent : Shadow.c.world.getEntities()) {
            if (ent instanceof PlayerEntity player && aplayers.getThis()) {
                if (distanceTo(player) < range.getThis() && player != Shadow.c.player) {
                    if (antibot.getThis()) {
                        if (!isBot(player)) {
                            targets.add(ent);
                        }
                    } else {
                        targets.add(ent);
                    }
                }
            }
            if (isHostile(ent) && hostile.getThis()) {
                if (distanceTo(ent) < range.getThis()) {
                    targets.add(ent);
                }
            }
            if (isPassive(ent) && passive.getThis()) {
                if (distanceTo(ent) < range.getThis()) {
                    targets.add(ent);
                }
            }
        }
        return targets;
    }

    private boolean isBot(PlayerEntity player) {
        try {
            PlayerListEntry playerListEntry = Shadow.c.getNetworkHandler().getPlayerListEntry(player.getUuid());
            return playerListEntry.getGameMode() == null;
        } catch (NullPointerException e) {
            return true;
        }
    }

    public double distanceTo(Entity e) {
        float f = (float) (Shadow.c.player.getX() - e.getX());
        float g = (float) (Shadow.c.player.getY() - e.getY());
        float h = (float) (Shadow.c.player.getZ() - e.getZ());
        return Math.sqrt(f * f + g * g + h * h);
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (rotmode.getThis().equalsIgnoreCase("stay")) {
                if (rotations == null) {
                    if (tgt == null) return;
                    Vec3d center = tgt.getBoundingBox().getCenter();
                    rotations = calculateLookAt(center.x, center.y, center.z, Shadow.c.player);
                }
                PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();
                if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
                    return;
                ((PlayerMovePacketMixin)packet).setYaw((float)rotations[0]);
                ((PlayerMovePacketMixin)packet).setPitch((float)rotations[1]);
                rotations = null;
            }
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (Entity target : targetlist) {
            RenderUtils.renderEntity(target, new Color(53, 53, 53, 100), matrix);
        }
    }
}
