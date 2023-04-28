package net.shadow.plugin.quake;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadow.feature.ModuleRegistry;
import net.shadow.mixin.ILivingEntityAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuakeClientPlayer {

    private static final Random random = new Random();
    private static final List<double[]> baseVelocities = new ArrayList<>();

    private static double getSpeed(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        return MathHelper.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
    }

    private static float getSlipperiness(PlayerEntity player) {
        float f2 = 0.91F;
        if (player.isOnGround()) {
            BlockPos groundPos = new BlockPos(MathHelper.floor(player.getX()), MathHelper.floor(player.getBoundingBox().minY) - 1, MathHelper.floor(player.getZ()));
            Block ground = player.world.getBlockState(groundPos).getBlock();

            f2 = ground.getSlipperiness() * 0.91F;
        }
        return f2;
    }

    private static float minecraft_getMoveSpeed(PlayerEntity player) {
        float f2 = getSlipperiness(player);

        float f3 = 0.16277136F / (f2 * f2 * f2);

        return player.getMovementSpeed() * f3;
    }

    private static double[] getMovementDirection(PlayerEntity player, double sidemove, double forwardmove) {
        double f3 = sidemove * sidemove + forwardmove * forwardmove;
        double[] dir = {0.0F, 0.0F};

        if (f3 >= 1.0E-4F) {
            f3 = MathHelper.sqrt((float) f3);

            if (f3 < 1.0F) {
                f3 = 1.0F;
            }

            f3 = 1.0F / f3;
            sidemove *= f3;
            forwardmove *= f3;
            double f4 = MathHelper.sin(player.getYaw() * (float) Math.PI / 180.0F);
            double f5 = MathHelper.cos(player.getYaw() * (float) Math.PI / 180.0F);
            dir[0] = (sidemove * f5 - forwardmove * f4);
            dir[1] = (forwardmove * f5 + sidemove * f4);
        }

        return dir;
    }

    private static float quake_getMoveSpeed(PlayerEntity player) {
        float baseSpeed = player.getMovementSpeed();
        return !player.isSneaking() ? baseSpeed * 2.15F : baseSpeed * 1.11F;
    }

    private static float quake_getMaxMoveSpeed(PlayerEntity player) {
        float baseSpeed = player.getMovementSpeed();
        return baseSpeed * 2.15F;
    }

    private static void spawnBunnyhopParticles(PlayerEntity player, int numParticles) {
        // taken from sprint
        int j = MathHelper.floor(player.getX());
        int i = MathHelper.floor(player.getY() - 0.20000000298023224D - player.getHeightOffset());
        int k = MathHelper.floor(player.getZ());
        BlockState blockState = player.world.getBlockState(new BlockPos(j, i, k));

        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            for (int iParticle = 0; iParticle < numParticles; iParticle++) {
                player.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), player.getX() + (random.nextFloat() - 0.5D) * player.getWidth(), player.getY() + 0.1D, player.getZ() + (random.nextFloat() - 0.5D) * player.getWidth(), -player.getVelocity().x * 4.0D, 1.5D, -player.getVelocity().z * 4.0D);
            }
        }
    }

    private static boolean isJumping(PlayerEntity player) {
        return ((ILivingEntityAccessor) player).isJumping();
    }

    private static void minecraft_ApplyGravity(PlayerEntity player) {
        BlockPos pos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
        double velocityY = player.getVelocity().y;
        if (player.world.isClient && !player.world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
            if (player.getY() > 0.0D) {
                velocityY = -0.1D;
            } else {
                velocityY = 0.0D;
            }
        } else {
            // gravity
            velocityY -= 0.08D;
        }

        // air resistance
        velocityY *= 0.9800000190734863D;
        player.setVelocity(player.getVelocity().x, velocityY, player.getVelocity().z);
    }

    private static void minecraft_ApplyFriction(PlayerEntity player, float momentumRetention) {
        player.setVelocity(player.getVelocity().multiply(momentumRetention, 1, momentumRetention));
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public static boolean quake_travel(PlayerEntity player, Vec3d movementInput) {
        // take care of ladder movement using default code
        if (player.isClimbing()) {
            return false;
        } else if ((player.isInLava() && !player.getAbilities().flying)) {
            return false;
        } else if (player.isSubmergedInWater() && !player.getAbilities().flying) {
            return false;
        } else {
            // get all relevant movement values
            float wishspeed = (movementInput.x != 0.0D || movementInput.z != 0.0D) ? quake_getMoveSpeed(player) : 0.0F;
            double[] wishdir = getMovementDirection(player, movementInput.x, movementInput.z);
            boolean onGroundForReal = player.isOnGround() && !isJumping(player);
            float momentumRetention = getSlipperiness(player);

            // ground movement
            if (onGroundForReal) {
                // apply friction before acceleration so we can accelerate back up to maxspeed afterwards
                //quake_Friction(); // buggy because material-based friction uses a totally different format
                minecraft_ApplyFriction(player, momentumRetention);

                double sv_accelerate = (double) ModuleRegistry.find("QuakeSpeed").config.get("Ground Accel").getThis();

                if (wishspeed != 0.0F) {
                    // alter based on the surface friction
                    sv_accelerate *= minecraft_getMoveSpeed(player) * 2.15F / wishspeed;

                    quake_Accelerate(player, wishspeed, wishdir[0], wishdir[1], sv_accelerate);
                }

                if (!baseVelocities.isEmpty()) {
                    float speedMod = wishspeed / quake_getMaxMoveSpeed(player);
                    // add in base velocities
                    for (double[] baseVel : baseVelocities) {
                        player.setVelocity(player.getVelocity().add(baseVel[0] * speedMod, 0, baseVel[1] * speedMod));
                    }
                }
            }
            // air movement
            else {
                double sv_airaccelerate = (double) ModuleRegistry.find("QuakeSpeed").config.get("Air Accel").getThis();
                quake_AirAccelerate(player, wishspeed, wishdir[0], wishdir[1], sv_airaccelerate);
            }

            // apply velocity
            player.move(MovementType.SELF, player.getVelocity());

            // HL2 code applies half gravity before acceleration and half after acceleration, but this seems to work fine
            minecraft_ApplyGravity(player);
        }
        return true;
    }

    private static void quake_Jump(PlayerEntity player) {
        quake_ApplySoftCap(player, quake_getMaxMoveSpeed(player));

        boolean didTrimp = quake_DoTrimp(player);

        if (!didTrimp) {
            quake_ApplyHardCap(player, quake_getMaxMoveSpeed(player));
        }
    }

    private static boolean quake_DoTrimp(PlayerEntity player) {
        if ((Boolean) ModuleRegistry.find("QuakeSpeed").config.get("Trimping Enabled").getThis() && player.isSneaking()) {
            double curspeed = getSpeed(player);
            float movespeed = quake_getMaxMoveSpeed(player);
            if (curspeed > movespeed) {
                double speedbonus = curspeed / movespeed * 0.5F;
                if (speedbonus > 1.0F)
                    speedbonus = 1.0F;

                player.setVelocity(player.getVelocity().add(0, speedbonus * curspeed * 1.4D, 0));

                float mult = 0.71428571428f;
                player.setVelocity(player.getVelocity().multiply(mult, 1, mult));

                spawnBunnyhopParticles(player, 30);

                return true;
            }
        }

        return false;
    }

    private static void quake_Accelerate(PlayerEntity player, float wishspeed, double wishX, double wishZ, double accel) {
        double addspeed, accelspeed, currentspeed;

        // Determine veer amount
        // this is a dot product
        currentspeed = player.getVelocity().x * wishX + player.getVelocity().z * wishZ;

        // See how much to add
        addspeed = wishspeed - currentspeed;

        // If not adding any, done.
        if (addspeed <= 0)
            return;

        // Determine acceleration speed after acceleration
        accelspeed = accel * wishspeed / getSlipperiness(player) * 0.05F;

        // Cap it
        if (accelspeed > addspeed)
            accelspeed = addspeed;

        // Adjust pmove vel.
        player.setVelocity(player.getVelocity().add(accelspeed * wishX, 0, accelspeed * wishZ));
    }

    private static void quake_AirAccelerate(PlayerEntity player, float wishspeed, double wishX, double wishZ, double accel) {
        double addspeed, accelspeed, currentspeed;

        float wishspd = wishspeed;
        float maxAirAcceleration = (float) 0.045D;

        if (wishspd > maxAirAcceleration)
            wishspd = maxAirAcceleration;

        // Determine veer amount
        // this is a dot product
        currentspeed = player.getVelocity().x * wishX + player.getVelocity().z * wishZ;

        // See how much to add
        addspeed = wishspd - currentspeed;

        // If not adding any, done.
        if (addspeed <= 0)
            return;

        // Determine acceleration speed after acceleration
        accelspeed = accel * wishspeed * 0.05F;

        // Cap it
        if (accelspeed > addspeed)
            accelspeed = addspeed;

        // Adjust pmove vel.
        player.setVelocity(player.getVelocity().add(accelspeed * wishX, 0, accelspeed * wishZ));
    }

    private static void quake_ApplySoftCap(PlayerEntity player, float movespeed) {
        float softCapPercent = 1.4f;
        float softCapDegen = 0.6f;

        if ((Boolean) ModuleRegistry.find("QuakeSpeed").config.get("Uncapped B-Hop").getThis()) {
            softCapPercent = 1.0F;
            softCapDegen = 1.0F;
        }

        float speed = (float) (getSpeed(player));
        float softCap = movespeed * softCapPercent;

        // apply soft cap first; if soft -> hard is not done, then you can continually trigger only the hard cap and stay at the hard cap
        if (speed > softCap) {
            if (softCapDegen != 1.0F) {
                float applied_cap = (speed - softCap) * softCapDegen + softCap;
                float multi = applied_cap / speed;
                player.setVelocity(player.getVelocity().multiply(multi, 1, multi));
            }

            spawnBunnyhopParticles(player, 10);
        }
    }

    private static void quake_ApplyHardCap(PlayerEntity player, float movespeed) {
        if ((Boolean) ModuleRegistry.find("QuakeSpeed").config.get("Uncapped B-Hop").getThis())
            return;

        float hardCapPercent = 2.0f;

        float speed = (float) (getSpeed(player));
        float hardCap = movespeed * hardCapPercent;

        if (speed > hardCap && hardCap != 0.0F) {
            float multi = hardCap / speed;
            player.setVelocity(player.getVelocity().multiply(multi, 1, multi));

            spawnBunnyhopParticles(player, 30);
        }
    }

    public static boolean travel(PlayerEntity player, Vec3d movementInput) {
        if (!player.world.isClient)
            return false;

        if (!ModuleRegistry.find("QuakeSpeed").isEnabled())
            return false;

        boolean didQuakeMovement;
        double d0 = player.getX();
        double d1 = player.getY();
        double d2 = player.getZ();

        if ((player.getAbilities().flying || player.isFallFlying()) && !player.hasVehicle()) return false;
        else didQuakeMovement = quake_travel(player, movementInput);

        if (didQuakeMovement)
            player.increaseTravelMotionStats(player.getX() - d0, player.getY() - d1, player.getZ() - d2);

        return didQuakeMovement;
    }

    public static void beforeOnLivingUpdate(PlayerEntity player) {
        if (!player.world.isClient)
            return;

        if (!baseVelocities.isEmpty()) {
            baseVelocities.clear();
        }
    }

    public static void afterJump(PlayerEntity player) {
        if (!player.world.isClient)
            return;

        if (!ModuleRegistry.find("QuakeSpeed").isEnabled())
            return;

        // undo this dumb thing
        if (player.isSprinting()) {
            float f = player.getYaw() * 0.017453292F;
            Vec3d deltaVelocity = new Vec3d(MathHelper.sin(f) * 0.2F, 0, -(MathHelper.cos(f) * 0.2F));
            player.setVelocity(player.getVelocity().add(deltaVelocity));
        }

        quake_Jump(player);
    }

    public static boolean updateVelocity(Entity entity, Vec3d movementInput, float movementSpeed) {
        if (!(entity instanceof PlayerEntity))
            return false;

        return updateVelocityPlayer((PlayerEntity) entity, movementInput, movementSpeed);
    }

    public static boolean updateVelocityPlayer(PlayerEntity player, Vec3d movementInput, float movementSpeed) {
        if (!player.world.isClient)
            return false;

        if (!ModuleRegistry.find("QuakeSpeed").isEnabled())
            return false;

        if ((player.getAbilities().flying && !player.hasVehicle()) || player.isSubmergedInWater() || player.isInLava() || !player.getAbilities().flying) {
            return false;
        }

        // this is probably wrong, but its what was there in 1.10.2
        float wishspeed = movementSpeed;
        wishspeed *= 2.15f;
        double[] wishdir = getMovementDirection(player, movementInput.x, movementInput.z);
        double[] wishvel = new double[]{wishdir[0] * wishspeed, wishdir[1] * wishspeed};
        baseVelocities.add(wishvel);

        return true;
    }

    private float getSurfaceFriction(PlayerEntity player) {
        float f2 = 1.0F;

        if (player.isOnGround()) {
            BlockPos groundPos = new BlockPos(MathHelper.floor(player.getX()), MathHelper.floor(player.getBoundingBox().minY) - 1, MathHelper.floor(player.getZ()));
            Block ground = player.world.getBlockState(groundPos).getBlock();
            f2 = 1.0F - ground.getSlipperiness();
        }

        return f2;
    }

    private void quake_ApplyWaterFriction(PlayerEntity player, double friction) {
        player.setVelocity(player.getVelocity().multiply(friction));

    }

    @SuppressWarnings("unused")
    private void quake_WaterAccelerate(PlayerEntity player, float wishspeed, float speed, double wishX, double wishZ, double accel) {
        float addspeed = wishspeed - speed;
        if (addspeed > 0) {
            float accelspeed = (float) (accel * wishspeed * 0.05F);
            if (accelspeed > addspeed) {
                accelspeed = addspeed;
            }

            Vec3d newVelocity = player.getVelocity().add(accelspeed * wishX, 0, accelspeed * wishZ);
            player.setVelocity(newVelocity);
        }
    }

    private void quake_WaterMove(PlayerEntity player, float sidemove, float upmove, float forwardmove) {
        double lastPosY = player.getY();

        // get all relevant movement values
        float wishspeed = (sidemove != 0.0F || forwardmove != 0.0F) ? quake_getMaxMoveSpeed(player) : 0.0F;
        double[] wishdir = getMovementDirection(player, sidemove, forwardmove);
        double curspeed = getSpeed(player);

        // water jump
        if (player.horizontalCollision && player.doesNotCollide(player.getVelocity().x, player.getVelocity().y + 0.6000000238418579D - player.getY() + lastPosY, player.getVelocity().z)) {
            player.setVelocity(player.getVelocity().x, 0.30000001192092896D, player.getVelocity().z);
        }

        if (!baseVelocities.isEmpty()) {
            float speedMod = wishspeed / quake_getMaxMoveSpeed(player);
            // add in base velocities
            for (double[] baseVel : baseVelocities) {
                player.setVelocity(player.getVelocity().add(baseVel[0] * speedMod, 0, baseVel[1] * speedMod));
            }
        }
    }

    @SuppressWarnings("unused")
    private void quake_Friction(PlayerEntity player) {
        double speed, newspeed, control;
        float friction;
        float drop;

        // Calculate speed
        speed = getSpeed(player);

        // If too slow, return
        if (speed <= 0.0F) {
            return;
        }

        drop = 0.0F;

        // convars
        float sv_friction = 1.0F;
        float sv_stopspeed = 0.005F;

        float surfaceFriction = getSurfaceFriction(player);
        friction = sv_friction * surfaceFriction;

        // Bleed off some speed, but if we have less than the bleed
        //  threshold, bleed the threshold amount.
        control = (speed < sv_stopspeed) ? sv_stopspeed : speed;

        // Add the amount to the drop amount.
        drop += control * friction * 0.05F;

        // scale the velocity
        newspeed = speed - drop;
        if (newspeed < 0.0F)
            newspeed = 0.0F;

        if (newspeed != speed) {
            // Determine proportion of old speed we are using.
            newspeed /= speed;
            // Adjust velocity according to proportion.
            player.setVelocity(player.getVelocity().multiply(newspeed, 1, newspeed));

        }
    }

    public interface IsJumpingGetter {
        boolean isJumping();
    }

    /* =================================================
     * END QUAKE PHYSICS
     * =================================================
     */
}