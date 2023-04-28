package net.shadow.feature.command;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;

public class KillCmd extends Command {
    public KillCmd() {
        super("kill", "kill people in creative in your render distance");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(Utils.getPlayersFromWorld());
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (!Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("You must be in creative mode");
            return;
        }
        if (args.length < 1) {
            ChatUtils.message("Please Provide a player");
            return;
        }
        String plr = PlayerUtils.completeName(args[0]);
        Entity target = PlayerUtils.getEntity(plr);
        if (target == null) {
            ChatUtils.message("Cannot Find Entity, They must be in render distance!");
            return;
        }
        Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
        ItemStack stack = new ItemStack(item, 1);
        ItemStack before = Shadow.c.player.getMainHandStack();
        NbtCompound tag = CreativeUtils.parse("{EntityTag:{id:\"minecraft:area_effect_cloud\",Particle:\"block air\",Radius:4.5f,Pos:[" + target.getX() + "," + (target.getY() + 1) + "," + target.getZ() + ",],RadiusPerTick:0f,RadiusOnUse:0f,Duration:40,DurationOnUse:-999f,Age:0,WaitTime:0,Effects:[{Id:6b,Amplifier:125b,Duration:1980,ShowParticles:0b}]}}");
        stack.setNbt(tag);
        CreativeUtils.setSlot(36 + Shadow.c.player.getInventory().selectedSlot, stack);
        Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
        CreativeUtils.setSlot(36 + Shadow.c.player.getInventory().selectedSlot, before);
        ChatUtils.message("Killed Player " + plr);
    }
}
