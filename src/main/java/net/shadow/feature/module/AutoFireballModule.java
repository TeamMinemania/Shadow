package net.shadow.feature.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.RenderListener;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.plugin.RaycastPlugin;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoFireballModule extends Module implements RenderListener {
    SliderValue radius = this.config.create("Radius", 1, 1, 100, 1);
    BlockPos walkman = new BlockPos(0, 0, 0);
    List<BlockPos> targets = new ArrayList<>();

    final Block[] blocks = new Block[]{Blocks.COBBLESTONE, Blocks.GLASS, Blocks.GLASS_PANE, Blocks.OAK_DOOR, Blocks.IRON_DOOR, Blocks.BRICKS, Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.WHITE_WOOL, Blocks.BLACK_WOOL, Blocks.BARREL, Blocks.CHEST, Blocks.CRAFTING_TABLE, Blocks.FURNACE};

    public AutoFireballModule() {
        super("AutoFireball", "auto nuke shit", ModuleType.GRIEF);
    }

    @Override
    public void onEnable() {
        targets.clear();
        Shadow.getEventSystem().add(RenderListener.class, this);
        ChatUtils.message("Working...");
        BlockPos before = Shadow.c.player.getBlockPos();
        new Thread(() -> {
            int l = (int) Math.round(radius.getThis()) * 2;
            for (int x = -l; x < l; x++)
                for (int y = -l; y < l; y++)
                    for (int z = -l; z < l; z++) {
                        BlockPos pos = before.add(new BlockPos(x, y, z));
                        Block block = Shadow.c.world.getBlockState(pos).getBlock();
                        for (Block b : blocks) {
                            if (b.equals(block)) {
                                boolean executebreak = false;
                                for (BlockPos bl : targets) {
                                    if (distanceToBlocks(bl, pos) < 10) {
                                        executebreak = true;
                                    }
                                }
                                if (!executebreak) {
                                    targets.add(pos);
                                }
                            }
                        }
                    }
            ItemStack b4 = Shadow.c.player.getMainHandStack();
            for (BlockPos nuke : targets) {
                ItemStack fireball = new ItemStack(Items.BLAZE_SPAWN_EGG, 1);
                try {
                    fireball.setNbt(StringNbtReader.parse("{EntityTag:{id:\"minecraft:fireball\",ExplosionPower:25b,Pos:[" + nuke.getX() + ".0," + nuke.getY() + ".9," + nuke.getZ() + ".0],power:[0.0,-1.0,0.0]}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.swingHand(Hand.MAIN_HAND);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, fireball));
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.0, 0.0, 0.0), Direction.UP, Shadow.c.player.getBlockPos(), true)));
                Utils.sleep(75);
                RaycastPlugin.raycast(nuke);
                Utils.sleep(75);
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, b4));
            this.setEnabled(false);
        }).start();

    }

    private double distanceToBlocks(BlockPos bl, BlockPos b) {
        Vec3d a = new Vec3d(bl.getX(), bl.getY(), bl.getZ());
        Vec3d bc = new Vec3d(b.getX(), b.getY(), b.getZ());
        return a.distanceTo(bc);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (BlockPos globalpos : targets) {
            Vec3d vp = new Vec3d(globalpos.getX() - 1.5, globalpos.getY() - 1.5, globalpos.getZ() - 1.5);
            RenderUtils.renderObject(vp, new Vec3d(3, 3, 3), new Color(53, 53, 53, 100), matrix);
        }
        Vec3d vp = new Vec3d(walkman.getX() - 1.5, walkman.getY() - 1.5, walkman.getZ() - 1.5);
        RenderUtils.renderObject(vp, new Vec3d(3, 3, 3), new Color(255, 255, 255, 255), matrix);
    }
}
