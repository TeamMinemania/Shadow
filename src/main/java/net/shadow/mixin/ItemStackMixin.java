package net.shadow.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.module.BetterItemInfo;
import net.shadow.feature.module.BetterTooltipModule;
import net.shadow.feature.module.other.Unload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cr) {
        if (!Unload.loaded) return;

        List<Text> l = cr.getReturnValue();
        ItemStack stack = (ItemStack) ((Object) this);
        if (BetterTooltipModule.turnedOn() && BetterTooltipModule.ison("size")) {
            if (stack.hasNbt()) {
                float fs;
                NbtCompound ct = stack.getNbt();
                String key = ct.asString();
                fs = key.getBytes(StandardCharsets.UTF_8).length;
                l.add(Text.of("§7Size: " + getBytes(fs)));
            } else {
                l.add(Text.of("§7Size: null"));
            }
        }
        if (ModuleRegistry.getAll().stream().filter(module -> module.getClass() == BetterItemInfo.class).findFirst().get().isEnabled()) {
            if (stack.hasNbt()) {
                stack.getNbt().putInt("HideFlags", 2);
                NbtCompound c = stack.getNbt();
                NbtList attributeModifiers = (NbtList) c.get("AttributeModifiers");
                if (attributeModifiers == null) return;
                attributeModifiers.forEach(attr -> {
                    NbtCompound attribute = (NbtCompound) attr;
                    String name = attribute.getString("AttributeName");
                    String operation = "";
                    int value = attribute.getInt("Amount");
                    switch (attribute.getInt("Operation")) {
                        case 0:
                            operation = "";
                            break;
                        case 1:
                            operation = "%";
                            break;
                        case 2:
                            operation = "x";
                            break;

                    }
                    l.add(Text.of("§7Name: §f" + name));
                    l.add(Text.of("§7Value: §f" + value + operation));
                    l.add(Text.of(""));

                });


            }
        }

        if (BetterTooltipModule.turnedOn() && BetterTooltipModule.ison("creator")) {
            if (stack.hasNbt()) {
                String creator = stack.getNbt().getString("creator");
                if (creator != null && !creator.equals(" ") && !creator.equals("")) {
                    l.add(Text.of("§7Creator: " + creator));
                } else {
                    l.add(Text.of("§7Creator: unset"));
                }
            } else {
                l.add(Text.of("§7Creator: unset"));
            }
        }
        if (BetterTooltipModule.turnedOn() && BetterTooltipModule.ison("info")) {
            if (stack.hasNbt()) {
                String info = stack.getNbt().getString("info");
                if (info != null && !info.equals(" ") && !info.equals("")) {
                    l.add(Text.of("§7Info: " + info));
                } else {
                    l.add(Text.of("§7Info: unset"));
                }
            } else {
                l.add(Text.of("§7Info: unset"));
            }
        }
        if (BetterTooltipModule.turnedOn() && BetterTooltipModule.ison("spawns")) {
            if (stack.hasNbt()) {
                NbtCompound e = stack.getNbt().getCompound("EntityTag");
                if (e == null) return;
                String info = e.getString("id");
                if (info != null && !info.equals(" ") && !info.equals("")) {
                    l.add(Text.of("§7Spawns: " + info));
                }
            }
        }
    }


    String getBytes(float bytes) {
        if (Math.abs(bytes) < 1024) {
            return Math.round(bytes) + " B";
        } else {
            return Math.round(bytes) / 1024 + " KB";
        }
    }
}
