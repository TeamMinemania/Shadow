package net.shadow.feature.module;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;

public class PlayerFinderModule extends Module implements PacketInput {
    final MultiValue mode = this.config.create("Mode", "SoundEvents", "SoundEvents", "Totalfreedom", "Creative");
    final CustomValue<String> name = this.config.create("Name", "Notch");

    public PlayerFinderModule() {
        super("PlayerFinder", "find players", ModuleType.OTHER);
    }

    @Override
    public String getVanityName() {
        return this.getName() + " [" + mode.getThis() + "]";
    }

    @Override
    public void onEnable() {
        if (mode.getThis().equalsIgnoreCase("creative")) {
            new Thread(() -> {
                try {
                    String player = PlayerUtils.completeName(name.getThis());
                    ItemStack hbefore = Shadow.c.player.getMainHandStack();
                    ItemStack s = new ItemStack(Items.WRITTEN_BOOK, 1);
                    s.setNbt(StringNbtReader.parse("{pages:['{\"text\":\"nbtexample\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"nbt\":\"Pos\",\"entity\":\"" + player + "\"}]}}'],title:\"\",author:\"\",resolved:0b}"));
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, s));
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                    Thread.sleep(200);
                    ItemStack ustack = Shadow.c.player.getMainHandStack();
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, hbefore));
                    String nbt = ustack.getNbt().asString();
                    nbt = nbt.replace("{author:\"\",pages:['{\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"", "");
                    nbt = nbt.replace("\"}},\"text\":\"nbtexample\"}'],resolved:1b,title:\"\"}", ""); //[-202.69999998807907d,99.0d,67.66826379181784d]
                    nbt = nbt.replace("\u00a7", ""); //why
                    nbt = nbt.replace("[", "");
                    nbt = nbt.replace("]", "");
                    nbt = nbt.replace("d", "");
                    String[] coords = nbt.split(",");
                    ChatUtils.message(player + " is located at " + coords[0] + " " + coords[1] + " " + coords[2]);
                    //this is so bad it wants to make me puke
                    //i hate it

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Shadow.c.setScreen(null);
            }).start();
            this.setEnabled(false);
        }
        if (mode.getThis().equalsIgnoreCase("totalfreedom")) {
            new Thread(() -> {
                String fullname = PlayerUtils.completeName(name.getThis());
                ItemStack current = Shadow.c.player.getMainHandStack();
                BlockHitResult breakcoords = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, Shadow.c.player.getBlockPos(), false);
                ItemStack sign = new ItemStack(Items.OAK_SIGN, 1);
                try {
                    sign.setNbt(StringNbtReader.parse("{BlockEntityTag:{Text1:'{\"nbt\":\"Pos\",\"entity\":\"" + fullname + "\"}',Text2:'{\"nbt\":\"Dimension\",\"entity\":\"" + fullname + "\"}'}}"));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    ChatUtils.message(e.getMessage());
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, sign));
                try {
                    Thread.sleep(300);
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, breakcoords));
                try {
                    Thread.sleep(300);
                } catch (Exception ignored) {
                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, current));
            }).start();

        }
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public String getSpecial() {
        if (this.isEnabled() && mode.getThis().equalsIgnoreCase("totalfreedom")) {
            return "freedom";
        } else {
            return "none";
        }
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet && mode.getThis().equalsIgnoreCase("soundevents")) {
            SoundEvent e = packet.getSound();
            if (SoundEvents.ENTITY_WITHER_SPAWN.equals(e)) {
                ChatUtils.message("Coords logged: " + (int) packet.getX() + ", " + (int) packet.getY() + ", " + (int) packet.getZ() + " [Wither Kill]");
            } else if (SoundEvents.ENTITY_ENDER_DRAGON_DEATH.equals(e)) {
                ChatUtils.message("Coords logged: " + (int) packet.getX() + ", " + (int) packet.getY() + ", " + (int) packet.getZ() + " [Dragon Death]");
            } else if (SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER.equals(e)) {
                ChatUtils.message("Coords logged: " + (int) packet.getX() + ", " + (int) packet.getY() + ", " + (int) packet.getZ() + " [Lightning Hit]");
            } else if (SoundEvents.BLOCK_END_PORTAL_SPAWN.equals(e)) {
                ChatUtils.message("Coords logged: " + (int) packet.getX() + ", " + (int) packet.getY() + ", " + (int) packet.getZ() + " [End Portal Light]");
            }
        }
    }
}
