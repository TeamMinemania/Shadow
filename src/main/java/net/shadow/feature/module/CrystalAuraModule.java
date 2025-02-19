package net.shadow.feature.module;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;
import net.shadow.Shadow;
import net.shadow.event.events.EntitySpawn;
import net.shadow.event.events.PacketInput;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CrystalAuraModule extends Module implements PacketOutput, EntitySpawn, PacketInput, RenderListener {

    final SliderValue bRange = this.config.create("Break Range", 4, 1, 7, 1);
    final SliderValue pRange = this.config.create("Place Range", 4, 1, 7, 1);
    private final LinkedHashMap<Vec3d, Long> placedCrystals = new LinkedHashMap<>();
    private final LinkedHashMap<EndCrystalEntity, AtomicInteger> spawnedCrystals = new LinkedHashMap<>();
    private final List<EndCrystalEntity> lostCrystals = new ArrayList<>();
    private final long placeTimer = -1;
    public BlockPos target = null;
    private long renderTimer = -1;
    private long breakTimer = -1;
    private long cleanupTimer = -1;
    private double[] rotations = null;

    //crystalaura code thanks to ares client
    public CrystalAuraModule() {
        super("CrystalAura", "Place and blow up crystals automatically", ModuleType.COMBAT);
    }

    // damage calculations
    private static float getDamage(Vec3d vec3d, PlayerEntity entity) {
        float f2 = 12.0f;
        double d7 = Math.sqrt(entity.squaredDistanceTo(vec3d)) / f2;
        if (d7 <= 1.0D) {
            double d8 = entity.getX() - vec3d.x;
            double d9 = entity.getEyeY() - vec3d.y;
            double d10 = entity.getZ() - vec3d.z;
            double d11 = Math.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
            if (d11 != 0.0D) {
                double d12 = Explosion.getExposure(vec3d, entity);
                double d13 = (1.0D - d7) * d12;
                float damage = transformForDifficulty((float) ((int) ((d13 * d13 + d13) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                damage = DamageUtil.getDamageLeft(damage, (float) entity.getArmor(), (float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
                damage = getReduction(entity, damage, DamageSource.GENERIC);
                return damage;
            }
        }
        return 0.0f;
    }

    private static float transformForDifficulty(float f) {
        if (Shadow.c.world.getDifficulty() == Difficulty.PEACEFUL) f = 0.0F;
        if (Shadow.c.world.getDifficulty() == Difficulty.EASY) f = Math.min(f / 2.0F + 1.0F, f);
        if (Shadow.c.world.getDifficulty() == Difficulty.HARD) f = f * 3.0F / 2.0F;
        return f;
    }

    // get blast reduction off armor and potions
    private static float getReduction(PlayerEntity player, float f, DamageSource damageSource) {
        if (player.hasStatusEffect(StatusEffects.RESISTANCE) && damageSource != DamageSource.OUT_OF_WORLD) {
            int i = (player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
            int j = 25 - i;
            float f1 = f * (float) j;
            float f2 = f;
            f = Math.max(f1 / 25.0F, 0.0F);
            float f3 = f2 - f;
            if (f3 > 0.0F && f3 < 3.4028235E37F) {
                if (player instanceof ServerPlayerEntity) {
                    player.increaseStat(Stats.DAMAGE_RESISTED, Math.round(f3 * 10.0F));
                } else if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) damageSource.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f3 * 10.0F));
                }
            }
        }

        if (f <= 0.0F) {
            return 0.0F;
        } else {
            int k = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), damageSource);
            if (k > 0) {
                f = DamageUtil.getInflictedDamage(f, (float) k);
            }

            return f;
        }
    }

    // raytracing
    private static HitResult.Type rayTrace(Vec3d start, Vec3d end) {
        double minX = Math.min(start.x, end.x);
        double minY = Math.min(start.y, end.y);
        double minZ = Math.min(start.z, end.z);
        double maxX = Math.max(start.x, end.x);
        double maxY = Math.max(start.y, end.y);
        double maxZ = Math.max(start.z, end.z);

        for (double x = minX; x > maxX; x += 1) {
            for (double y = minY; y > maxY; y += 1) {
                for (double z = minZ; z > maxZ; z += 1) {
                    BlockState blockState = Shadow.c.world.getBlockState(new BlockPos(x, y, z));

                    if (blockState.getBlock() == Blocks.OBSIDIAN
                            || blockState.getBlock() == Blocks.BEDROCK
                            || blockState.getBlock() == Blocks.BARRIER)
                        return HitResult.Type.BLOCK;
                }
            }
        }

        return HitResult.Type.MISS;
    }

    public static int findItemInHotbar(Item item) {
        int index = -1;
        for (int i = 0; i < 9; i++) {
            if (Shadow.c.player.getInventory().getStack(i).getItem() == item) {
                index = i;
                break;
            }
        }
        return index;
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
        Shadow.getEventSystem().add(EntitySpawn.class, this);
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(EntitySpawn.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        run();
    }

    private void run() {
        // reset rotations
        if (rotations != null) rotations = null;

        // cleanup render
        if ((System.nanoTime() / 1000000) - renderTimer >= 3000) {
            target = null;
            renderTimer = System.nanoTime() / 1000000;
        }

        // do logic
        boolean offhand = Shadow.c.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
        place(offhand);
        explode(offhand);

        // cleanup place map and lost crystals every ten seconds
        if ((System.nanoTime() / 1000000) - cleanupTimer >= 10000) {
            lostCrystals.removeIf(crystal -> Shadow.c.world.getEntityById(crystal.getId()) == null);

            // cleanup crystals that never spawned
            Optional<Map.Entry<Vec3d, Long>> first = placedCrystals.entrySet().stream().findFirst();
            if (first.isPresent()) {
                Map.Entry<Vec3d, Long> entry = first.get();
                if ((System.nanoTime() / 1000000) - entry.getValue() >= 10000) placedCrystals.remove(entry.getKey());
            }
            cleanupTimer = System.nanoTime() / 1000000;
        }
    }

    private void place(boolean offhand) {
        // find best crystal spot
        BlockPos target = getBestPlacement();
        if (target == null) return;

        placeCrystal(offhand, target);
    }

    private void placeCrystal(boolean offhand, BlockPos pos) {
        // switch to crystals if not holding
        if (!offhand && Shadow.c.player.getInventory().getMainHandStack().getItem() != Items.END_CRYSTAL) {
            int slot = findItemInHotbar(Items.END_CRYSTAL);
            if (slot != -1) {
                Shadow.c.player.getInventory().selectedSlot = slot;
                Shadow.c.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(Shadow.c.player.getInventory().selectedSlot));
            }
        }
        int slot = findItemInHotbar(Items.END_CRYSTAL);
        if (slot == -1) {
            return;
        }

        // place
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5f, 0.5f, 0.5f), Direction.UP, pos, false)));
        rotations = calculateLookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Shadow.c.player);

        // add to place map
        placedCrystals.put(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), System.nanoTime() / 1000000);

        // set render pos
        target = pos;
    }

    private void explode(boolean offhand) {
        if (!shouldBreakCrystal(offhand)) return;
        try {
            for (Map.Entry<EndCrystalEntity, AtomicInteger> entry : spawnedCrystals.entrySet()) {
                // check if crystal can be broken
                if (!canBreakCrystal(entry.getKey())) continue;

                breakCrystal(entry.getKey(), offhand);

                // remove if it hits limit of tries
                if (entry.getValue().get() + 1 == 5) {
                    lostCrystals.add(entry.getKey());
                    spawnedCrystals.remove(entry.getKey());
                } else entry.getValue().set(entry.getValue().get() + 1);
            }
        } catch (ConcurrentModificationException e) {
            spawnedCrystals.clear();
        }
    }

    private boolean isPartOfHole(BlockPos pos) {
        List<Entity> entities = new ArrayList<>();
        entities.addAll(Shadow.c.world.getOtherEntities(Shadow.c.player, new Box(pos.add(1, 0, 0))));
        entities.addAll(Shadow.c.world.getOtherEntities(Shadow.c.player, new Box(pos.add(-1, 0, 0))));
        entities.addAll(Shadow.c.world.getOtherEntities(Shadow.c.player, new Box(pos.add(0, 0, 1))));
        entities.addAll(Shadow.c.world.getOtherEntities(Shadow.c.player, new Box(pos.add(0, 0, -1))));
        return entities.stream().anyMatch(entity -> entity instanceof PlayerEntity);
    }

    private boolean shouldOffhand() {
        return Shadow.c.player.getMainHandStack().getItem() == Items.END_CRYSTAL;
    }

    private boolean shouldBreakCrystal(boolean offhand) {
        return (System.nanoTime() / 1000000) - breakTimer >= 25;
    }

    private boolean canBreakCrystal(EndCrystalEntity crystal) {
        return Shadow.c.player.distanceTo(crystal) <= bRange.getThis() // check range
                && !(Shadow.c.player.getHealth() - getDamage(crystal.getPos(), Shadow.c.player) <= 1); // check suicide
    }

    private void breakCrystal(EndCrystalEntity crystal, boolean offhand) {
        // find hand
        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;

        // break
        Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, false));
        Shadow.c.interactionManager.attackEntity(Shadow.c.player, crystal);

        //spoof rotations
        rotations = calculateLookAt(crystal.getX() + 0.5, crystal.getY() + 0.5, crystal.getZ() + 0.5, Shadow.c.player);

        // reset timer
        breakTimer = System.nanoTime() / 1000000;
    }

    private BlockPos getBestPlacement() {
        double bestScore = 69420;
        BlockPos target = null;
        for (PlayerEntity targetedPlayer : getTargets()) {
            // find best location to place
            List<BlockPos> targetsBlocks = getPlaceableBlocks(targetedPlayer);
            List<BlockPos> blocks = getPlaceableBlocks(Shadow.c.player);

            for (BlockPos pos : blocks) {
                if (!targetsBlocks.contains(pos) || (double) getDamage(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), targetedPlayer) < 4)
                    continue;

                double score = getScore(pos, targetedPlayer);

                if (target == null || (score < bestScore && score != -1)) {
                    target = pos;
                    bestScore = score;
                }
            }
        }
        return target;
    }

    // utils
    private double getScore(BlockPos pos, PlayerEntity player) {
        double score;
        score = 200 - getDamage(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), player);

        return score;
    }

    private List<PlayerEntity> getTargets() {
        List<PlayerEntity> targets = new ArrayList<>();

        for (PlayerEntity entityPlayer : Shadow.c.world.getPlayers()) {
            if (entityPlayer.getHealth() == 0 || Shadow.c.player.distanceTo(entityPlayer) > Math.max(pRange.getThis(), bRange.getThis()) + 8 || entityPlayer == Shadow.c.player)
                continue;
            targets.add(entityPlayer);
        }

        return targets;
    }

    private List<BlockPos> getPlaceableBlocks(PlayerEntity player) {
        List<BlockPos> square = new ArrayList<>();

        int range = (int) Math.round(pRange.getThis());

        BlockPos pos = player.getBlockPos();
        pos.add(new Vec3i(player.getVelocity().x, player.getVelocity().y, player.getVelocity().z));

        for (int x = -range; x <= range; x++)
            for (int y = -range; y <= range; y++)
                for (int z = -range; z <= range; z++)
                    square.add(pos.add(x, y, z));

        return square.stream().filter(blockPos -> canCrystalBePlacedHere(blockPos) && Shadow.c.player.squaredDistanceTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5) <= (range * range)).collect(Collectors.toList());
    }

    private boolean canCrystalBePlacedHere(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        return (Shadow.c.world.getBlockState(pos).getBlock() == Blocks.BEDROCK
                || Shadow.c.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
                && Shadow.c.world.getBlockState(boost).getBlock() == Blocks.AIR
                && Shadow.c.world.getNonSpectatingEntities(Entity.class, new Box(boost)).stream().allMatch(entity -> entity instanceof EndCrystalEntity
                && !lostCrystals.contains(entity));
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (rotations == null) return;
            if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
                return;
            event.cancel();
            double x = packet.getX(0);
            double y = packet.getY(0);
            double z = packet.getZ(0);

            Packet<?> newPacket;
            if (packet instanceof PlayerMoveC2SPacket.Full) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, (float) rotations[0], (float) rotations[1], packet.isOnGround());
            } else {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround((float) rotations[0], (float) rotations[1], packet.isOnGround());
            }


            Shadow.c.player.networkHandler.getConnection().send(newPacket);
        }
    }


    @Override
    public void onEntitySpawn(Entity Spawnd) {
        if (Spawnd instanceof EndCrystalEntity crystal) {
            // loop through all placed crystals to see if it matches
            for (Map.Entry<Vec3d, Long> entry : new ArrayList<>(placedCrystals.entrySet())) {
                if (entry.getKey().equals(crystal.getPos())) {
                    // break crystal if possible and add to spawned crystals map
                    boolean offhand = shouldOffhand();
                    if (shouldBreakCrystal(offhand) && canBreakCrystal(crystal)) {
                        breakCrystal(crystal, offhand);
                        spawnedCrystals.put(crystal, new AtomicInteger(1));
                    } else spawnedCrystals.put(crystal, new AtomicInteger(0));

                    // remove from placed list
                    placedCrystals.remove(entry.getKey());
                }
            }
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof EntitiesDestroyS2CPacket packet) {
            IntList idlist = packet.getEntityIds();
            for (int i = 0; i < idlist.size(); i++) {
                Entity removed = Shadow.c.world.getEntityById(idlist.getInt(i));
                if (removed instanceof EndCrystalEntity crystal) {
                    BlockPos pos = removed.getBlockPos().down();
                    if (canCrystalBePlacedHere(pos) && pos.equals(getBestPlacement()) && spawnedCrystals.containsKey(crystal))
                        placeCrystal(shouldOffhand(), pos);

                    spawnedCrystals.remove(crystal);
                }
            }
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (target == null) return;
        Vec3d vp = new Vec3d(target.getX(), target.getY(), target.getZ());
        RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(100, 100, 100, 125), matrix);
    }
}
