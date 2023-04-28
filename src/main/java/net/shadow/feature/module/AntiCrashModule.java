package net.shadow.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;

public class AntiCrashModule extends Module implements PacketInput {
    static boolean isnabled = false;
    static boolean ismap = false;
    private static int soundcount = 0;
    private static boolean shouldblockpoof = false;
    private static boolean shouldblockidentifier = false;
    private static boolean puffers = false;
    private static boolean shouldCap = false;
    private static int guicounter = 0;
    final BooleanValue sgui = this.config.create("Cap Screenguis", false);
    final BooleanValue nsl = this.config.create("Cap Sounds", true);
    final BooleanValue nsp = this.config.create("Cap Particles", true);
    final BooleanValue nen = this.config.create("Block Entity Names", true);
    final BooleanValue vel = this.config.create("Block Velocity", true);
    final BooleanValue noaoe = this.config.create("Block Particles", true);
    final BooleanValue nopoof = this.config.create("Block Poof", false);
    final BooleanValue noident = this.config.create("Block Identifier", false);
    final BooleanValue entitties = this.config.create("Cap Entities", false);
    final BooleanValue pufferss = this.config.create("Anti Puffer Crash", false);
    final SliderValue maxx = this.config.create("Max Entities", 1000, 100, 5000, 0);
    int ticks = 0;


    public AntiCrashModule() {
        super("AntiCrash", "prevents game crash exploits", ModuleType.RENDER);
    }

    public static boolean shouldCapAoes() {
        return shouldCap;
    }

    public static boolean shouldBlockPoof() {
        return shouldblockpoof;
    }

    public static boolean shouldBlockIdentifier() {
        return shouldblockidentifier;
    }

    public static boolean getPuffers() {
        return puffers;
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
        isnabled = true;
    }

    @Override
    public void onDisable() {
        shouldCap = false;
        shouldblockpoof = false;
        isnabled = false;
        shouldblockidentifier = false;
        puffers = false;
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {
        shouldblockpoof = nopoof.getThis();
        shouldCap = noaoe.getThis();
        shouldblockidentifier = noident.getThis();
        puffers = pufferss.getThis();
        try {
            if (nen.getThis()) {
                for (Entity entity : Shadow.c.world.getEntities()) {
                    if (nen.getThis()) {
                        entity.setCustomNameVisible(false);
                    }
                }
            }
            int ticount = 0;
            for (Entity e : Shadow.c.world.getEntities()) {
                ticount++;
            }
            if (ticount > maxx.getThis() && entitties.getThis()) {
                for (Entity e : Shadow.c.world.getEntities()) {
                    e.setRemoved(RemovalReason.DISCARDED);
                    e.remove(RemovalReason.DISCARDED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ticks++;
        if (ticks % 200 == 0) {
            guicounter = 0;
        }
        if (ticks % 20 == 0) {
            soundcount = 0;
        }
        isnabled = true;
    }

    @Override
    public void onRender() {
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        ClientPlayerEntity player = Shadow.c.player;
        if (nsl.getThis()) {
            if (event.getPacket() instanceof PlaySoundS2CPacket) {
                if (soundcount > 200) {
                    event.cancel();
                    return;
                }
                soundcount++;
                if (soundcount > 200) {
                    ChatUtils.breakerPop("Sound Limit Reached");
                    event.cancel();
                }
            }
        }
        if (nsp.getThis()) {
            if (event.getPacket() instanceof ParticleS2CPacket packet) {
                int count = packet.getCount();
                float speed = packet.getSpeed();
                if (count >= 999 || speed >= 300) {
                    ChatUtils.breakerPop("Dropped Particles [" + count + "] [" + speed + "]");
                    event.cancel();
                }
            }
        }
        if (sgui.getThis()) {
            if (event.getPacket() instanceof OpenScreenS2CPacket) {
                guicounter++;
                if (guicounter > 101) {
                    event.cancel();
                    return;
                }
                if (guicounter > 100) {
                    ChatUtils.breakerPop("Gui Limit Reached, blocking...");
                    event.cancel();
                    guicounter++;
                }
            }
        }
        if (vel.getThis()) {
            if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket p) {
                double vx = p.getVelocityX() / 800d;
                double vy = p.getVelocityY() / 800d;
                double vz = p.getVelocityZ() / 800d;
                if (vx > 500 || vy > 500 || vz > 500) {
                    ChatUtils.breakerPop("Dropped Malicious Velocity Packet");
                }
            }
        }


    }
}
