package net.shadow.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.feature.ModuleRegistry;

@Mixin(GenericContainerScreen.class)
public abstract class ContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler>{

    public ContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @org.spongepowered.asm.mixin.Shadow
	@Final
	private int rows;

    @Override
	protected void init()
	{
		super.init();
		

        if(ModuleRegistry.find("ChestVoider").isEnabled()){
            this.addDrawableChild(new ButtonWidget(x + backgroundWidth - 108, y + 4, 100, 12, Text.of("Void"), b -> {
                shiftClickSlots(0, rows * 9, 1);
            }));
        }

	}

    private void shiftClickSlots(int from, int to, int mode)
	{
		
		for(int i = from; i < to; i++)
		{
			Slot slot = handler.slots.get(i);
			if(slot.getStack().isEmpty())
				continue;
			
			
            Shadow.c.interactionManager.clickSlot(Shadow.c.player.currentScreenHandler.syncId, slot.id, 120, SlotActionType.SWAP, Shadow.c.player);
		}
	}
}
