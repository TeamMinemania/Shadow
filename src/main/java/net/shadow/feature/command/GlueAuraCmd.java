package net.shadow.feature.command;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import net.shadow.feature.module.movement.GlueAuraModule;
import net.shadow.plugin.NotificationSystem;

import java.util.List;

import javax.management.Notification;

import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;
import net.shadow.utils.Utils;


public class GlueAuraCmd extends Command {
    public GlueAuraCmd() {
        super("glueaura", "activate glu aura");
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
        ModuleRegistry.find("GlueAura").setEnabled(true);
        Entity player = PlayerUtils.getEntityFromWorldByViewName(PlayerUtils.completeName(args[0]));
        if(player != null){
            ((GlueAuraModule)ModuleRegistry.find("GlueAura")).updateEntity(player);
        }else{
            ((GlueAuraModule)ModuleRegistry.find("GlueAura")).updateTarget(PlayerUtils.completeName(args[0]));
            NotificationSystem.post("GlueAura", "Cant find that player so we're waiting for them to appear");
        }
    }
}
