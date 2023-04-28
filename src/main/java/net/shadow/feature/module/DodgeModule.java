package net.shadow.feature.module;

import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;
import net.shadow.plugin.NotificationSystem;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class DodgeModule extends Module implements PacketInput{

    BooleanValue predict = this.config.create("Predict", false);
    Random random = new Random();
    boolean sus = false;

    public DodgeModule() {
        super("Dodge", "dodge damage sources", ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        Shadow.getEventSystem().add(PacketInput.class, this);
    }

    @Override
    public void onDisable() {
        Shadow.getEventSystem().remove(PacketInput.class, this);
    }

    @Override
    public void onUpdate() {

        if(sus) return;
        if(predict.getThis()){
            for(Entity e : Shadow.c.world.getEntities()){
                if(isHostile(e)){
                    if(e.getPos().distanceTo(Shadow.c.player.getPos()) < 5){
                        sus = true;
                        NotificationSystem.post("Dodge", "Dodging..");
                        Shadow.tpService.gooberTP(Shadow.c.player.getPos(), getDestination(), () -> {sus = false;});
                    }
                }
            }
        }
    }

    private boolean checkUp(double up, Vec3d start){
        boolean flag = false;
        for(int i = 0; i < up; i++){
            if(!Shadow.c.world.getBlockState(new BlockPos(start.x, start.y + i, start.z)).isAir()){
                flag = true;
            }
        }
        return flag;
    }

    private Vec3d offsetToTop(Vec3d origin){
        Vec3d editor = origin;
        while(!Shadow.c.world.getBlockState(new BlockPos(editor.x, editor.y, editor.z)).isAir()){
            editor = editor.add(new Vec3d(0,1,0));
        }
        return editor;
    }

    private Vec3d getDestination() {
        Vec3d destination = new Vec3d(randomInteger(), 50, randomInteger());
        destination = offsetToTop(destination);
        if(!checkUp(102, destination)){
            return getDestination();
        }
        return destination;
    }

    private double randomInteger(){
        if(random.nextBoolean()){
            return random.nextDouble(100);
        }else{
            return random.nextDouble(100) * -1;
        }
    }

    @Override
    public void onRender() {

    }

    public static boolean isHostile(Entity entity) {
        if (entity instanceof IronGolemEntity)
            return ((IronGolemEntity) entity).getAngryAt() == Shadow.c.player.getUuid() && ((IronGolemEntity) entity).getAngryAt() != null;
        else if (entity instanceof WolfEntity)
            return ((WolfEntity) entity).isAttacking() && ((WolfEntity) entity).getOwner() != Shadow.c.player;
        else if (entity instanceof PiglinEntity) return ((PiglinEntity) entity).isAngryAt(Shadow.c.player);
        else if (entity instanceof EndermanEntity) return ((EndermanEntity) entity).isAngry();
        return entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet){
            if (packet.getId() == Shadow.c.player.getId()) {
                NotificationSystem.post("Dodge", "Dodging..");
                Shadow.tpService.gooberTP(Shadow.c.player.getPos(), getDestination(), () -> {});
            }
        }
    }
}
