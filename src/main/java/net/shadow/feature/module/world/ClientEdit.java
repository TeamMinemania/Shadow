package net.shadow.feature.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.event.events.*;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.HashMap;

public class ClientEdit extends Module implements PacketOutput, LeftClick, RightClick, RenderListener, MiddleClick {
    static BlockPos a = new BlockPos(0, 0, 0);
    static BlockPos b = new BlockPos(0, 0, 0);
    static HashMap<BlockPos, BlockState> clipboard = new HashMap<>();
    final CustomValue<String> block = this.config.create("Quick Fill", "air");

    public ClientEdit() {
        super("ClientEdit", "client side world edit", ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketOutput.class, this);
        Shadow.getEventSystem().add(RightClick.class, this);
        Shadow.getEventSystem().add(MiddleClick.class, this);
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketOutput.class, this);
        Shadow.getEventSystem().remove(RightClick.class, this);
        Shadow.getEventSystem().remove(MiddleClick.class, this);
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        if (Shadow.c.options.attackKey.isPressed()) {
            if (Shadow.c.player.getMainHandStack().getItem().equals(Items.DIAMOND_SWORD)) {
                BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
                b = blockHitResult.getBlockPos();
            }
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket c) {
            String d = c.getChatMessage();
            String[] e = d.split(" ");
            switch (e[0].toLowerCase()) {
                case "//help" -> {
                    event.cancel();
                    ChatUtils.message("//set [material] - set the selection to a material");
                    ChatUtils.message("//hollow [material] - make a hollow box out of the material");
                    ChatUtils.message("//pos1 - set the first selection point");
                    ChatUtils.message("//pos2 - set the second selection point");
                    ChatUtils.message("//wand - get the wand");
                    ChatUtils.message("//replacenear [radius] [material1] [material2] - replace all blocks near you");
                }
                case "//copy" -> event.cancel();
                case "//set" -> {
                    event.cancel();
                    try {
                        ChatUtils.message("Filled Blocks");
                        Shadow.c.player.sendChatMessage("/fill " + a.getX() + " " + a.getY() + " " + a.getZ() + " " + b.getX() + " " + b.getY() + " " + b.getZ() + " minecraft:" + e[1]);
                    } catch (Exception f) {
                        ChatUtils.message("you didnt put a material");
                    }
                }
                case "//hollow" -> {
                    event.cancel();
                    try {
                        ChatUtils.message("Filled Blocks");
                        Shadow.c.player.sendChatMessage("/fill " + a.getX() + " " + a.getY() + " " + a.getZ() + " " + b.getX() + " " + b.getY() + " " + b.getZ() + " minecraft:" + e[1] + " outline");
                    } catch (Exception f) {
                        ChatUtils.message("you didnt put a material");
                    }
                }
                case "//pos1" -> {
                    event.cancel();
                    a = Shadow.c.player.getBlockPos();
                    ChatUtils.message("Updated Position 1");
                }
                case "//pos2" -> {
                    event.cancel();
                    b = Shadow.c.player.getBlockPos();
                    ChatUtils.message("Updated Position 2");
                }
                case "//wand" -> {
                    event.cancel();
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.DIAMOND_SWORD, 1)));
                    ChatUtils.message("Gave Wand");
                }
                case "//replacenear" -> {
                    event.cancel();
                    try {
                        Integer size = Integer.valueOf(e[1]);
                        for (int x = size * -1; x < size; x++)
                            for (int y = size * -1; y < size; y++)
                                for (int z = size * -1; z < size; z++) {
                                    BlockPos pos = Shadow.c.player.getBlockPos().add(new BlockPos(x, y, z));
                                    if (Shadow.c.world.getBlockState(pos).getBlock().equals(Registry.BLOCK.get(new Identifier(e[2])))) {
                                        Shadow.c.player.sendChatMessage("/setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " minecraft:" + e[3]);
                                    }
                                }
                    } catch (Exception f) {
                        ChatUtils.message("incorrect usage, use //replacenear [radius] [material1] [material2]");
                    }
                }
                case "//replace" -> {
                    event.cancel();
                    try {
                        ChatUtils.message("Filled Blocks");
                        Shadow.c.player.sendChatMessage("/fill " + a.getX() + " " + a.getY() + " " + a.getZ() + " " + b.getX() + " " + b.getY() + " " + b.getZ() + " minecraft:" + e[2] + " replace minecraft:" + e[1]);
                    } catch (Exception f) {
                        ChatUtils.message("you didnt put a material");
                    }
                }
                case "//center" -> {
                    event.cancel();
                    try {
                        int dimx = Math.abs(a.getX() - b.getX());
                        int dimy = Math.abs(a.getY() - b.getY());
                        int dimz = Math.abs(a.getZ() - b.getZ());
                        Shadow.c.player.sendChatMessage("/setblock " + a.getX() + (dimx / 2) + " " + a.getY() + (dimy / 2) + " " + a.getZ() + (dimz / 2) + " minecraft:" + e[1]);
                    } catch (Exception f) {
                        ChatUtils.message("incorrect usage, use //center [material]");
                    }
                }
                case "//push" -> {
                    event.cancel();
                    try {
                        Integer offset = Integer.parseInt(e[1]);
                        Vec3d rots = Vec3d.fromPolar(Shadow.c.player.getPitch(), Shadow.c.player.getYaw()).normalize();
                        Vec3d floored = new Vec3d(Math.round(rots.x), Math.round(rots.y), Math.round(rots.z));
                        a = new BlockPos(a.getX() + (floored.x * offset), a.getY() + (floored.y * offset), a.getZ() + (floored.z * offset));
                        b = new BlockPos(b.getX() + (floored.x * offset), b.getY() + (floored.y * offset), b.getZ() + (floored.z * offset));
                    } catch (Exception abcd) {
                        ChatUtils.message("incorrect usage, use //push [offset]");
                    }
                }
            }
        }
    }

    @Override
    public void onRightClick(RightClickEvent event) {
        if (Shadow.c.player.getMainHandStack().getItem().equals(Items.DIAMOND_SWORD)) {
            event.cancel();
            BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
            a = blockHitResult.getBlockPos();
            event.cancel();
        }
    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        if (Shadow.c.player.getMainHandStack().getItem().equals(Items.DIAMOND_SWORD)) {
            BlockHitResult blockHitResult = (BlockHitResult) Shadow.c.player.raycast(100, Shadow.c.getTickDelta(), true);
            b = blockHitResult.getBlockPos();
            event.cancel();
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        RenderUtils.renderObject(new Vec3d(a.getX(), a.getY(), a.getZ()), new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        RenderUtils.renderObject(new Vec3d(b.getX(), b.getY(), b.getZ()), new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Vec3d(a.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Vec3d(a.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Vec3d(b.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Vec3d(b.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Vec3d(b.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Vec3d(a.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Vec3d(a.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Vec3d(b.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5), new Vec3d(a.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, a.getY() + 0.5, a.getZ() + 0.5), new Vec3d(b.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(b.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Vec3d(a.getX() + 0.5, b.getY() + 0.5, a.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
        RenderUtils.vector(new Vec3d(a.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Vec3d(b.getX() + 0.5, a.getY() + 0.5, b.getZ() + 0.5), new Color(53, 53, 53, 255), matrix, 1);
    }

    @Override
    public void onMiddleClick(MiddleClickEvent event) {
        ChatUtils.message("Filled Blocks");
        Shadow.c.player.sendChatMessage("/fill " + a.getX() + " " + a.getY() + " " + a.getZ() + " " + b.getX() + " " + b.getY() + " " + b.getZ() + " minecraft:" + block.getThis());
    }
}
