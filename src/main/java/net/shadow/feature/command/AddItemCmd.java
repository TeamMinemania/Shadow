package net.shadow.feature.command;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.Requests;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class AddItemCmd extends Command {

    Requests requests = new Requests();

    public AddItemCmd() {
        super("additem", "adds an item to the online registry");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"exploits", "grief"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message("Added item!");
        ItemStack hand = Shadow.c.player.getMainHandStack();
        String encodedPayload = Base64.getEncoder().encodeToString(hand.getNbt().asString().getBytes());
        try {
            requests.post("https://shadows.pythonanywhere.com/items/" + args[0] + "/add", "{\"name\":\"minecraft:" + Registry.ITEM.getId(hand.getItem()).getPath() + "\", \"count\":\"" + hand.getCount() + "\", \"nbt\":\"" + encodedPayload + "\"}");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
