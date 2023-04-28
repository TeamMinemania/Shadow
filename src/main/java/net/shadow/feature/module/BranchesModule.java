package net.shadow.feature.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BranchesModule extends Module implements RenderListener, PacketOutput {

    final List<Vec3d> renderpoints = new ArrayList<>();
    final List<player> movepoints = new ArrayList<>();
    final BooleanValue dubs = this.config.create("Double Speed", false);
    Vec3d block = new Vec3d(0, 0, 0);

    public BranchesModule() {
        super("Branches", "create \"branches\" of player movement", ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
        Shadow.getEventSystem().add(PacketOutput.class, this);
    }

    @Override
    public void onDisable() {
        new Thread(() -> {
            NotificationSystem.notifications.add(new Notification("Branches", "Merging branches", 150));
            Vec3d still = Shadow.c.player.getPos();
            for (player pos : movepoints) {
                Shadow.c.getNetworkHandler().getConnection().send(new PlayerMoveC2SPacket.Full(pos.position.x, pos.position.y, pos.position.z, pos.pitch, pos.yaw, pos.onGround));
                if (this.dubs.getThis()) {
                    Utils.sleep(25);
                } else {
                    Utils.sleep(50);
                }
                Shadow.c.player.updatePosition(still.x, still.y, still.z);
                block = pos.position;
            }
            block = new Vec3d(0, 0, 0);
            renderpoints.clear();
            movepoints.clear();
            Shadow.getEventSystem().remove(RenderListener.class, this);
            Shadow.getEventSystem().remove(PacketOutput.class, this);
            NotificationSystem.notifications.add(new Notification("Branches", "Branch has been merged", 150));
        }, "branchMerger").start();
    }

    @Override
    public void onUpdate() {
        movepoints.add(new player(Shadow.c.player.getPos(), Shadow.c.player.getPitch(), Shadow.c.player.getYaw(), Shadow.c.player.isOnGround()));
        if (Shadow.c.player.isSneaking()) {
            player origin = movepoints.get(0);
            movepoints.clear();
            renderpoints.clear();
            Shadow.c.player.updatePosition(origin.position.x, origin.position.y, origin.position.z);
            Shadow.c.player.setYaw(origin.yaw);
            Shadow.c.player.setPitch(origin.pitch);
            Shadow.c.player.setOnGround(origin.onGround);
            Shadow.c.player.setSneaking(false);
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        renderpoints.add(Shadow.c.player.getPos());
        for (int i = 1; i < renderpoints.size(); i++) {
            Vec3d current = renderpoints.get(i);
            Vec3d last = renderpoints.get(i - 1);
            RenderUtils.vector(last, current, new Color(25, 25, 25, 255), matrix, 1);
        }
        //render the block
        RenderUtils.renderObject(block.add(-0.25, -0.25, -0.25), new Vec3d(0.5, 0.5, 0.5), new Color(200, 200, 200, 255), matrix);
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.cancel();
        }
    }

    public record player(Vec3d position, float pitch, float yaw, boolean onGround) {

    }
}
