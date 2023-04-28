package net.shadow.feature.command;

import net.minecraft.util.math.BlockPos;
import net.shadow.Shadow;
import net.shadow.event.events.PacketOutput;
import net.shadow.feature.base.Command;
import net.shadow.utils.Utils;

import java.util.List;
import java.util.Random;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MoveCmd extends Command implements PacketOutput{

    boolean catching = false;

    public MoveCmd() {
        super("teleport", "move the");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"x"});
        }
        if(index ==1){
            return List.of(new String[]{"y"});
        }
        if(index ==2){
            return List.of(new String[]{"z"});
        }
        if(index ==3){
            return List.of(new String[]{"100"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        BlockPos pos = argsToPos(args);
        Shadow.getEventSystem().add(PacketOutput.class, this);
        new Thread(() -> {
            Vec3d origin = Shadow.c.player.getPos();
            Vec3d destination = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

            double distance = origin.distanceTo(destination);
            double steps = distance / 6;
            Vec3d delta = destination.subtract(origin).multiply(1/steps);

            catching = true;
            

            for(int i = 0; i < steps - 1; i++){
                catching = false;
                Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * i), origin.y + (delta.y * i) + randomFlick(), origin.z + (delta.z * i), false));
                catching = true;
                Utils.sleep(Long.parseLong(args[3]));
            }
            catching = false;
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps - 1), origin.y + (delta.y * steps), origin.z + (delta.z * steps - 1), false));
            catching = true;
            Utils.sleep(Long.parseLong(args[3]));
            catching = false;
            Shadow.c.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(origin.x + (delta.x * steps), origin.y + (delta.y * steps), origin.z + (delta.z * steps), false));
            catching = true;
            catching = false;
            Shadow.c.player.updatePosition(destination.getX(), destination.getY(), destination.getZ());
            Shadow.getEventSystem().remove(PacketOutput.class, this);
        }).start();
    }

    private BlockPos argsToPos(String... xyz) {
        BlockPos playerPos = new BlockPos(Shadow.c.player.getPos());
        int[] player =
                new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()};
        int[] pos = new int[3];
        try {
            for (int i = 0; i < 3; i++)

                if (xyz[i].equals("~"))
                    pos[i] = player[i];
                else if (xyz[i].startsWith("~"))
                    pos[i] = player[i] + Integer.parseInt(xyz[i].substring(1));
                else
                    pos[i] = Integer.parseInt(xyz[i]);

            return new BlockPos(pos[0], pos[1], pos[2]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onSentPacket(PacketOutputEvent event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket packet){
            if(catching){
                event.cancel();
            }
        }
    }

    double randomFlick(){
        return new Random().nextDouble(1);
    }
}
