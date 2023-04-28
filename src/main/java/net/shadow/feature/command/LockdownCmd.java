package net.shadow.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class LockdownCmd extends Command {
    public LockdownCmd() {
        super("lockdown", "place server takeover command blocks");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"op", "deop", "unban", "ban", "crash", "unbanip"});
        }
        return List.of(Utils.getPlayersFromWorld());
    }

    @Override
    public void call(String[] args) {
        Item item;
        ItemStack stack;
        StringBuilder lockdownnbt;
        String f1n;
        String stringfinaliser;
        String stringappender;
        if (args.length < 2) {
            ChatUtils.message("Please use the format >Lockdown <op/deop/unban/ban/crash/unbanip> <players>");
            return;
        }
        for (int i = 1; i < args.length - 1; i++) {
            args[i] = PlayerUtils.completeName(args[i]);
        }
        switch (args[0].toLowerCase()) {
            case "op" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + eyeval + " ~ repeating_command_block{Command:\\\\\\\"/execute run op " + args[i + 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"},";

                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + f1n + " ~ repeating_command_block{Command:\\\\\\\"/execute run op " + args[args.length - 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
                placeStackInHotbar(stack);
            }
            case "unban" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + eyeval + " ~ repeating_command_block{Command:\\\\\\\"/execute run pardon " + args[i + 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"},";

                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + f1n + " ~ repeating_command_block{Command:\\\\\\\"/execute run pardon " + args[args.length - 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
                placeStackInHotbar(stack);
            }
            case "deop" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~1 ~ repeating_command_block{Command:\\\\\\\"/execute run deop @a[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "name=!" + args[i + 1] + ",";
                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "name=!" + args[args.length - 1] + "]\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
                placeStackInHotbar(stack);
            }
            case "ban" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~1 ~ repeating_command_block{Command:\\\\\\\"/execute run ban @a[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "name=!" + args[i + 1] + ",";
                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "name=!" + args[args.length - 1] + "]\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
                placeStackInHotbar(stack);
            }
            case "crash" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~1 ~ repeating_command_block{Command:\\\\\\\"/execute as @a[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "name=!" + args[i + 1] + ",";
                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "name=!" + args[args.length - 1] + "] at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 force @s\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
                placeStackInHotbar(stack);
            }
            case "unbanip" -> {
                item = Registry.ITEM.get(new Identifier("command_block"));
                stack = new ItemStack(item, 1);
                lockdownnbt = new StringBuilder("{BlockEntityTag:{powered:0b,auto:1b,conditionMet:1b,Command:\"/summon falling_block ~ ~2 ~ {BlockState:{Name:\\\"minecraft:redstone_block\\\"},Time:20,Passengers:[{id:\\\"minecraft:armor_stand\\\",Health:0f,Passengers:[{id:\\\"minecraft:falling_block\\\",BlockState:{Name:\\\"minecraft:activator_rail\\\"},Time:20,Passengers:[");
                for (int i = 0; i < args.length - 2; i++) {
                    int eye = i + 1;
                    String eyeval = String.valueOf(eye);
                    stringappender = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + eyeval + " ~ repeating_command_block{Command:\\\\\\\"/execute run pardon " + args[i + 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"},";

                    lockdownnbt.append(stringappender);
                }
                f1n = String.valueOf(args.length - 1);
                stringfinaliser = "{id:\\\"minecraft:command_block_minecart\\\",Command:\\\"/setblock ~ ~" + f1n + " ~ repeating_command_block{Command:\\\\\\\"/execute run pardon-ip " + args[args.length - 1] + "\\\\\\\",powered:0b,auto:1b,conditionMet:1b} replace\\\"}]}]}]}\"}}";
                lockdownnbt.append(stringfinaliser);
                stack.setNbt(createNBT(lockdownnbt.toString()));
            }
        }
    }

    private NbtCompound createNBT(String nbt) {
        try {
            return StringNbtReader.parse(nbt);

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void placeStackInHotbar(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (!Shadow.c.player.getInventory().getStack(i).isEmpty())
                continue;

            Shadow.c.player.networkHandler.sendPacket(
                    new CreativeInventoryActionC2SPacket(36 + i, stack));
            ChatUtils.message("Item created.");
            return;
        }

        ChatUtils.message("Please clear a slot in your hotbar.");
    }
}
