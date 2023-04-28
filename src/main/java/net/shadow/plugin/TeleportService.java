package net.shadow.plugin;

import java.util.Random;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.event.events.PlayerMove;
import net.shadow.feature.base.Command;
import net.shadow.mixin.PlayerMovePacketMixin;
import net.shadow.utils.Utils;

public class TeleportService implements PacketOutput{
    
    boolean blocking = true;
    boolean registered = false;

    public TeleportService(){

    }


    public boolean getShouldBlockPackets(){
        return blocking;
    }

    public void splitter(Vec3d origin, Vec3d destination, Runnable after){
        new Thread(() -> {
            Shadow.getEventSystem().add(PacketOutput.class, this);
            blocking = true;

            double distance = origin.distanceTo(destination);
            double steps = distance / 6;
            Vec3d delta = destination.subtract(origin).multiply(1/steps);

            blocking = true;
            

            for(int i = 0; i < steps - 1; i++){
                blocking = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * i), origin.y + (delta.y * i), origin.z + (delta.z * i), true));
                blocking = true;
                Utils.sleep(10);
            }
            blocking = false;
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps - 1), origin.y + (delta.y * steps), origin.z + (delta.z * steps - 1), true));
            blocking = true;
            Utils.sleep(10);
            blocking = false;
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps), origin.y + (delta.y * steps), origin.z + (delta.z * steps), true));
            blocking = true;
            blocking = false;
            Shadow.getEventSystem().remove(PacketOutput.class, this);
            
            after.run();
        }).start();
    }


    public void gooberTP(Vec3d origin2, Vec3d destination, Runnable after){
        new Thread(() -> {
            Vec3d origin = origin2;
            Shadow.getEventSystem().add(PacketOutput.class, this);
            blocking = true;

            Vec3d originxz = new Vec3d(0, origin.y, 0);
            Vec3d destinationxz = new Vec3d(0, origin.y + 100, 0);
            double distance = originxz.distanceTo(destinationxz);
            double steps = distance / 5;
            Vec3d delta = destinationxz.subtract(originxz).multiply(1/steps);
            System.out.println(delta);

            for(int i = 0; i < steps; i++){
                blocking = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x, origin.y + (delta.y * i), origin.z, true));
                blocking = true;
                Utils.sleep(3);
            }

            origin = origin.add(new Vec3d(0, 100, 0));
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x, origin.y, origin.z, false));


            originxz = new Vec3d(origin.x, 0, origin.z);
            destinationxz = new Vec3d(destination.x, 0, destination.z);
            distance = originxz.distanceTo(destinationxz);
            steps = distance / 5;
            delta = destinationxz.subtract(originxz).multiply(1/steps);
            System.out.println(delta);

            for(int i = 0; i < steps; i++){
                blocking = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * i), origin.y, origin.z + (delta.z * i), false));
                blocking = true;
                Utils.sleep(3);
            }

            origin = origin.add(new Vec3d(delta.x * steps, 0, delta.z * steps));
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x, origin.y, origin.z, false));

            originxz = new Vec3d(0, origin.y, 0);
            destinationxz = new Vec3d(0, destination.y, 0);
            distance = originxz.distanceTo(destinationxz);
            steps = distance / 5;
            delta = destinationxz.subtract(originxz).multiply(1/steps);
            System.out.println(delta);

            for(int i = 0; i < steps; i++){
                blocking = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x, origin.y + (delta.y * i), origin.z, true));
                blocking = true;
                Utils.sleep(3);
            }
            blocking = false;
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(destination.x, destination.y, destination.z, true));
            blocking = true;

            Shadow.getEventSystem().remove(PacketOutput.class, this);
            after.run();
        }).start();
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            if(blocking){
                event.cancel();
                System.out.println("block packet");
            }else{
                System.out.println("no block");
                ((PlayerMovePacketMixin) packet).setY(packet.getY(0) + new Random().nextDouble(1));
            }
        }
        
    }
}
