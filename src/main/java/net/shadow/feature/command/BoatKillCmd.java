package net.shadow.feature.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.Utils;

import java.lang.module.ModuleReader;
import java.util.List;

public class BoatKillCmd extends Command {
    public BoatKillCmd() {
        super("boatkill", "kill ppl with boats");
    }


    @Override
    public void call(String[] args) {
        if(Shadow.c.player.getVehicle() == null){
            NotificationSystem.post("BoatKill", "You have to be riding a boat!");
            return;
        }

        Entity boat = Shadow.c.player.getVehicle();


        double prevy = boat.getY();

        for(int i = 0; i < 15; i++){
            boat.updatePosition(boat.getX(), prevy + (i * 7.4), boat.getZ());
            Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(boat));
        }

        boat.updatePosition(boat.getX(), prevy + 111, boat.getZ());
        Shadow.c.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(boat));

        //Shadow.c.player.networkHandler.sendPacket(new PlayerInputC2SPacket(0, 0, false, true));
        //Shadow.c.player.dismountVehicle();
//
        //ModuleRegistry.find("Nofall").setEnabled(true);
    }
}
