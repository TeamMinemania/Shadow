package net.shadow.feature.command;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class ItemDataCmd extends Command {
    public ItemDataCmd() {
        super("itemdata", "command to copy items from other players");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        if(index == 1){
            return List.of(new String[]{"hand", "offhand", "legs", "feet", "head", "chest"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length != 2) {
            ChatUtils.message("Please use the format >itemdata <player> <slot>");
            return;
        }


        String playerh = PlayerUtils.completeName(args[0]);
        AbstractClientPlayerEntity player = getPlayer(playerh);
        ItemStack item = getItem(player, args[1]);
        if (item == null || player == null) return;
        if (Shadow.c.player.getAbilities().creativeMode) {
            giveItem(item);
        } else {
            NbtCompound tag = item.getNbt();
            String nbt = tag == null ? "" : tag.asString();
            Shadow.c.keyboard.setClipboard(nbt);
            ChatUtils.message("You were not in creative mode, so the nbt was copied to your clipboard!");
        }


        ChatUtils.message("Item copied.");
    }

    private AbstractClientPlayerEntity getPlayer(String name) {
        for (AbstractClientPlayerEntity player : Shadow.c.world.getPlayers()) {
            if (!player.getEntityName().equalsIgnoreCase(name))
                continue;

            return player;
        }

        {
            ChatUtils.message("Player could not be found!");
            return null;
        }
    }

    private ItemStack getItem(AbstractClientPlayerEntity player, String slot) {
        switch (slot.toLowerCase()) {
            case "hand":
                return player.getInventory().getMainHandStack();

            case "offhand":
                return player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT);

            case "head":
                return player.getInventory().getArmorStack(3);

            case "chest":
                return player.getInventory().getArmorStack(2);

            case "legs":
                return player.getInventory().getArmorStack(1);

            case "feet":
                return player.getInventory().getArmorStack(0);

            default:
                ChatUtils.message("Please use the format >itemdata <player> <slot>");
                return null;
        }
    }

    private void giveItem(ItemStack stack) {
        int slot = Shadow.c.player.getInventory().getEmptySlot();
        if (slot < 0) {
            ChatUtils.message("Please clear a slot in your hotbar");
            return;
        }

        if (slot < 9)
            slot += 36;

        CreativeInventoryActionC2SPacket packet =
                new CreativeInventoryActionC2SPacket(slot, stack);
        Shadow.c.player.networkHandler.sendPacket(packet);
    }
}