package net.shadow.mixin;

import net.minecraft.text.HoverEvent;
import net.minecraft.util.Identifier;
import net.shadow.feature.module.AntiCrashModule;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@org.spongepowered.asm.mixin.Mixin(HoverEvent.EntityContent.class)
public class HoverEventMixin {
    @Redirect(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;", at = @At(
            value = "INVOKE",
            target = "Ljava/util/UUID;fromString(Ljava/lang/String;)Ljava/util/UUID;"
    ), require = 0)
    private static UUID redirect(String name) {
        UUID returnant = UUID.fromString("1-1-1-1-1");
        if (!AntiCrashModule.shouldBlockPoof()) return UUID.fromString(name);
        try {
            returnant = UUID.fromString(name);
        } catch (Exception ignored) {
        }
        return returnant;
    }

    @Redirect(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;", at = @At(
            value = "INVOKE",
            target = "Ljava/util/UUID;fromString(Ljava/lang/String;)Ljava/util/UUID;"
    ), require = 0)
    private static UUID redirect1(String name) {
        UUID returnant = UUID.fromString("1-1-1-1-1");
        if (!AntiCrashModule.shouldBlockPoof()) return UUID.fromString(name);
        try {
            returnant = UUID.fromString(name);
        } catch (Exception ignored) {
        }
        return returnant;
    }

    @Redirect(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;", at = @At(
            value = "NEW",
            target = "net/minecraft/util/Identifier"
    ))
    private static Identifier rip(String id) {
        if (Identifier.isValid(id)) return new Identifier(id);
        else return new Identifier("minecraft:bat");
    }

    @Redirect(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;", at = @At(
            value = "NEW",
            target = "net/minecraft/util/Identifier"
    ))
    private static Identifier stopserver(String id) {
        if (Identifier.isValid(id)) return new Identifier(id);
        else return new Identifier("minecraft:bat");
    }
}