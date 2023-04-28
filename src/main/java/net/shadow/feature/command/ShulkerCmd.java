package net.shadow.feature.command;

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

public class ShulkerCmd extends Command {
    public ShulkerCmd() {
        super("shulker", "perform operations with shulkers");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"lock", "unlock", "clear", "zip"});
        }
        if(index == 1){
            return List.of(new String[]{"<lockvalue/color>"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode to use this!");
            return;
        }

        if (args.length < 1) {
            ChatUtils.message("Please use the format >shulker <lock/unlock/clear/zip> <lockvalue/color>");
            return;
        }


        ItemStack item = Shadow.c.player.getInventory().getMainHandStack();
        switch (args[0].toLowerCase()) {

            case "lock":
                try {
                    if (args.length < 2) {
                        ChatUtils.message("Please provide a key value for the lock");
                        return;
                    }
                    NbtCompound tag = StringNbtReader.parse("{BlockEntityTag:{Lock:\"" + args[1] + "\"}}");
                    item.getNbt().copyFrom(tag);
                    ChatUtils.message("Shulker Locked with string " + args[1]);
                } catch (Exception e) {
                    ChatUtils.message("Hold a Shulker Box With Items In your hand");
                    return;
                }
                break;

            case "unlock":
                try {
                    NbtCompound tag = StringNbtReader.parse("{BlockEntityTag:{Lock:\"\"}}");
                    item.getNbt().copyFrom(tag);
                    ChatUtils.message("Shulker Unlocked");
                } catch (Exception e) {
                    ChatUtils.message("Hold a Shulker Box With Items In your hand");
                    return;
                }
                break;

            case "clear":
                try {
                    item.setNbt(StringNbtReader.parse("{BlockEntityTag:{LootTable:\"barter\"}}"));
                    ChatUtils.message("Shulker reset");
                } catch (Exception e) {
                    ChatUtils.message("Hold a Shulker Box With Items In your hand");
                    return;
                }
                break;

            case "zip":
                try {
                    if (args.length < 2) {
                        ChatUtils.message("Please provide a item to zip into");
                        return;
                    }
                    NbtCompound tag = item.getNbt();
                    String nbt = tag.asString();
                    nbt = nbt.replace("Infinity", "1.79769313486232E+308");
                    String shulkername = args[1];
                    Item boxitem = Registry.ITEM.get(new Identifier(shulkername));
                    ItemStack boxstack = new ItemStack(boxitem, 1);
                    boxstack.setNbt(StringNbtReader.parse(nbt));
                    Shadow.c.player.networkHandler.sendPacket(
                            new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, boxstack));
                    ChatUtils.message("Inventory zipped into shulker");
                } catch (Exception e) {
                    ChatUtils.message("Hold a Shulker Box With Items In your hand");
                    return;
                }
                break;
        }
    }
}
