package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;

import java.util.Base64;

public class BaseCmd extends Command {
    public BaseCmd() {
        super("base", "encode and decode items from base64");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"encode", "decode"});
        }
        return List.of(new String[0]);
    }

    public static String decodeBase642(String in) {
        String s = new String(Base64.getDecoder().decode(in));
        return new String(Base64.getDecoder().decode(s));
    }

    public static String encodeBase642(String in) {
        String s = Base64.getEncoder().encodeToString(in.getBytes());
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            {
                ChatUtils.message("Please use the format >base <encode/decode>");
                return;
            }
        }
        switch (args[0]) {
            case "decode" -> {
                ItemStack stack = Shadow.c.player.getMainHandStack();
                NbtCompound tag = stack.getNbt();
                String encodeddatastream = tag.getString("encode");
                String datastream = decodeBase642(encodeddatastream);
                try {
                    stack.setNbt(StringNbtReader.parse(datastream));
                } catch (Exception ignored) {

                }
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                ChatUtils.message("Decoded item source");
            }
            case "encode" -> {
                ItemStack stack2 = Shadow.c.player.getMainHandStack();
                String stringtag = stack2.getNbt().asString();
                if (stringtag == null) {
                    {
                        ChatUtils.message("Item has no nbt to encode");
                        return;
                    }
                }
                NbtCompound t = new NbtCompound();
                t.putString("encode", encodeBase642(stringtag));
                stack2.setNbt(t);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack2));
                ChatUtils.message("Encoded Item source");
            }
            default -> {
                ChatUtils.message("Please use the format >base <encode/decode>");
                return;
            }
        }
    }
}
