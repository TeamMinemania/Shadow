package net.shadow.feature.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;

import java.util.List;
import java.util.stream.StreamSupport;

public class AntiAnvilModule extends Module {
    public AntiAnvilModule() {
        super("AntiAnvil", "dont anvil", ModuleType.OTHER);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        //        Vec3d currentPos = Shadow.c.player.getPos();
        BlockPos currentPos = Shadow.c.player.getBlockPos();
        Vec3d ppos = Shadow.c.player.getPos();
        List<Entity> anvils = StreamSupport.stream(Shadow.c.world.getEntities().spliterator(), false).filter(entity -> {
            if (entity instanceof FallingBlockEntity e) {
                Block bs = e.getBlockState().getBlock();
                return bs == Blocks.ANVIL || bs == Blocks.CHIPPED_ANVIL || bs == Blocks.DAMAGED_ANVIL;
            }
            return false;
        }).toList();
        for (Entity anvil : anvils) {
            Vec3d anvilPos = anvil.getPos();
            BlockPos anvilBp = anvil.getBlockPos();
            if (anvilBp.getX() == currentPos.getX() && anvilBp.getZ() == currentPos.getZ()) {
                double yDist = anvilPos.y - ppos.y;
                if (yDist > 0 && yDist < -anvil.getVelocity().y * 2) {
                    PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 1, ppos.z, false);
                    Shadow.c.getNetworkHandler().sendPacket(p);
                }
            }
        }
    }

    @Override
    public void onRender() {

    }
}
